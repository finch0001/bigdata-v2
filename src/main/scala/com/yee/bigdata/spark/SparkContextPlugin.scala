package com.yee.bigdata.spark

import java.util.concurrent.atomic.AtomicLong
import scala.collection.mutable.Map
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.SQLContext

/**
  *  创建spark context
  * */
object SparkContextPlugin {
  private val sparkAppNum = new AtomicLong()

  /**
    *  方法功能:创建sparkConf
    * */
  private def createSparkConf(appName:String,master:String): SparkConf ={
      new SparkConf().setAppName(appName).setMaster(master)
  }

  /**
    * 方法功能: 创建spark context
    * @param  sparkPropertiesMap spark property map
    * @return sparkContext spark context
    */
  def createSparkContext(sparkPropertiesMap:Map[String,String]): SparkContext = {
    val appName = sparkPropertiesMap.getOrElse("spark.app.name","SparkApp_" + System.currentTimeMillis() + "_" + sparkAppNum.getAndIncrement())
    val master = sparkPropertiesMap.getOrElse("spark.master","local[2]")
    val logLevel = sparkPropertiesMap.getOrElse("spark.log.level","WARN")
    val sparkConf = createSparkConf(appName,master)

    sparkPropertiesMap.filter(f => {
      !f._1.trim.equals("spark.app.name") &&
      !f._1.trim.equals("spark.master") &&
      !f._1.trim.equals("spark.log.level")
    }).map(f => sparkConf.set(f._1,f._2))

    val sparkContext = new SparkContext(sparkConf)
    sparkContext.setLogLevel(logLevel)

    sparkContext
  }

  /**
    * 方法功能: 创建spark sql context
    * @param  sparkPropertiesMap spark property map
    * @return sqlContext spark sql context
    */
  def createSQLContext(sparkPropertiesMap:Map[String,String]): SQLContext = {
    val sc = createSparkContext(sparkPropertiesMap)
    new SQLContext(sc)
  }

  /**
    * 方法功能: 创建spark session
    * @param  sparkPropertiesMap spark property map
    * @return sparkSession spark session
    */
  def createSparkSession(sparkPropertiesMap:Map[String,String]): SparkSession = {
    val appName = sparkPropertiesMap.getOrElse("spark.app.name","SparkApp_" + System.currentTimeMillis() + "_" + sparkAppNum.getAndIncrement())
    val master = sparkPropertiesMap.getOrElse("spark.master","local[2]")
    val enableHiveSupport = sparkPropertiesMap.getOrElse("spark.enableHiveSupport","false")
    val logLevel = sparkPropertiesMap.getOrElse("spark.log.level","WARN")
    val sparkConf = createSparkConf(appName,master)

    sparkPropertiesMap.filter(f => {
        !f._1.trim.equals("spark.app.name") &&
        !f._1.trim.equals("spark.master") &&
        !f._1.trim.equals("spark.log.level") &&
        !f._1.trim.equals("spark.enableHiveSupport")
    }).map(f => sparkConf.set(f._1,f._2))

    val sparkSession = if(enableHiveSupport.toBoolean) SparkSession.builder.config(sparkConf).enableHiveSupport().getOrCreate()
    else SparkSession.builder.config(sparkConf).getOrCreate()

    sparkSession.sparkContext.setLogLevel(logLevel)

    sparkSession
  }

  /**
    *  方法功能: 关闭spark context
    *  @param sparkContext 需要关闭的spark context
    * */
  def stopSparkContext(sparkContext:SparkContext): Unit ={
    sparkContext.stop()
  }

  /**
    *  方法功能: 关闭spark sql context
    *  @param sqlContext 需要关闭的spark sql context
    * */
  def stopSparkSQLContext(sqlContext:SQLContext): Unit ={
    sqlContext.sparkContext.stop()
  }

  /**
    *  方法功能: 关闭spark session
    *  @param sparkSession 需要关闭的spark session
    * */
  def stopSparkSession(sparkSession:SparkSession): Unit ={
    sparkSession.stop()
  }

}
