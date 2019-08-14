package com.test

import java.util.concurrent.{CountDownLatch, TimeUnit}

import java.util.HashMap
import scala.collection.JavaConverters._
import scala.io.Source
import com.others.ZtAlgorithmUtil
import org.apache.spark.sql.SparkSession
import com.yee.bigdata.common.thread._
import com.yee.bigdata.spark._
import com.yee.bigdata.common.util.ServerProperty
import org.apache.spark.SparkContext

import scala.collection.mutable

object Main {

  /**
    * Returns the system properties map that is thread-safe to iterator over. It gets the
    * properties which have been set explicitly, as well as those for which only a default value
    * has been defined.
    */
  def getSystemProperties: Map[String, String] = {
    System.getProperties.stringPropertyNames().asScala
      .map(key => (key, System.getProperty(key))).toMap
  }

  def dataProcess(): Unit ={
    val rawFilePath = "E:\\tmp\\data\\a.txt"
    val source = Source.fromFile(rawFilePath,"UTF-8")
    val lineIterator = source.getLines()
    var preTeLong:Long = 0
    var teSum:Long = 0
    lineIterator.foreach(f => {
      val teLong = ZtAlgorithmUtil.dateTime2Long(f)
      var teDel:Long = 0
      if(preTeLong != 0){
        teDel = teLong - preTeLong
        teSum += teDel
        println(s"teDel:$teDel $f")
      }
      preTeLong = teLong
    })

    println(s"teSum:$teSum")
  }

  /**
    *  方法功能: 对输入的Double数值进行格式化处理,保留N位小数
    *  @param inputData 待格式化的Double数值
    *  @param digNum    保留的小数点位数
    *  @return outputData 格式化后的Double值
    * */
  def doubleFormat(inputData: Double,digNum:Int = 3) = {
    // 转换为BigDecimal
    val bigDecimal = BigDecimal(inputData)
    // 设置精度值
    val outputData = bigDecimal.setScale(digNum,BigDecimal.RoundingMode.HALF_UP).doubleValue()

    outputData
  }

  def digitFormat(): Unit ={
    val a:Double = 0.40259840260572605
    val b:Double = 0.09315017477985149
    val c:Double = 0.5042514226144225

    //val x = doubleFormat(21164997.toDouble/52570991,5)
    //val y = doubleFormat(26508997.toDouble/52570991,5)
    //val z = doubleFormat(4896997.toDouble/52570991,5)
    val x = doubleFormat(a,6)
    val y = doubleFormat(b,6)
    val z = doubleFormat(c,6)

    println(s"x:$x y:$y z:$z")

  }

  def threadUtilTest1(): Unit ={
    val executor = ThreadUtils.newDaemonSingleThreadExecutor("this-is-a-thread-name")
    @volatile var threadName = ""
    executor.submit(new Runnable {
      override def run(): Unit = {
        threadName = Thread.currentThread().getName()
        println(s"thread name:$threadName")
      }
    })
    executor.shutdown()
    executor.awaitTermination(10, TimeUnit.SECONDS)
    assert(threadName == "this-is-a-thread-name")
  }

  def threadUtilTest2(): Unit ={
    val executor = ThreadUtils.newDaemonSingleThreadScheduledExecutor("this-is-a-thread-name")

    try {
      val latch = new CountDownLatch(1)
      @volatile var threadName = ""
      executor.schedule(new Runnable {
        override def run(): Unit = {
          threadName = Thread.currentThread().getName()
          latch.countDown()
        }
      }, 30000, TimeUnit.MILLISECONDS)

      latch.await(10, TimeUnit.SECONDS)

      println("success!")

      assert(threadName == "this-is-a-thread-name")
    } finally {
      executor.shutdownNow()
    }
  }

  def createSparkSession(): SparkSession ={
    val availableCores = Runtime.getRuntime.availableProcessors()
    val master = s"local[$availableCores]"
    val sparkSession = SparkSession.builder.appName("MainTest")
      .master(master)
      //.config("spark.sql.warehouse.dir", "/user/hive/warehouse/")
      //.enableHiveSupport
      .getOrCreate

    sparkSession
  }

  def sparkContextPluginTest(): Unit ={
    val propFilePath = "E:\\yusheng\\personal\\github\\myproject\\bigdata\\src\\main\\resources\\sparkApp.properties"
    val props = ServerProperty.loadProps(propFilePath)
    val propsMap = ServerProperty.propsToStringMap(props)
    for(i <- 0 until 1){
      //val sparkContext = SparkContextPlugin.createSparkContext(propsMap.asScala)
      //println(sparkContext.getConf.toDebugString)
      //sparkContext.stop()

      val sqlContext = SparkContextPlugin.createSQLContext(propsMap.asScala)
      val df = sqlContext.sql("set -v")
      df.rdd.foreach(f => {
        val key = f.get(0).toString
        val value = f.get(1).toString
        val meaning = f.get(2).toString
        println(s"key:$key")
        println(s"value:$value")
        println(s"meaning:$meaning")
        println("---------------------------------------------------------------------\n")
      })

      sqlContext.sparkContext.stop()
      println("----------------------------------\n")
    }
  }

  def getSparkSQLSetVTest(): Unit ={
    val map = new HashMap[String,String]()
    val res = SparkUtils.getSparkSQLSetV(map.asScala)
    res.foreach(f => {
      val key = f._1
      val value = f._2
      val meaning = f._3
      println(s"key:$key")
      println(s"value:$value")
      println(s"meaning:$meaning")
      println("---------------------------------------------------------------------\n")
    })
  }

  def main(args:Array[String]): Unit ={
    //dataProcess()
    //digitFormat()
    //threadUtilTest1()
    //threadUtilTest2()
    //sparkContextPluginTest()
    //getSparkSQLSetVTest()


  }
}
