package com.yee.bigdata.spark

import com.yee.bigdata.spark.SparkContextPlugin

import scala.collection.mutable.Map

/**
  * spark工具类
  * */
object SparkUtils {

  /**
    * 方法功能：通过执行spark sql命令:set -v,获取当前spark session的配置属性值
    * @param sparkPropertiesMap 传入的spark属性值
    * @return 获取到的spark属性数组,列值分别为:key , value, meaning
    * */
  def getSparkSQLSetV(sparkPropertiesMap:Map[String,String]): Array[(String,String,String)] ={
    val sparkSession = SparkContextPlugin.createSparkSession(sparkPropertiesMap)
    val df = sparkSession.sql("set -v")
    df.rdd.collect().map(f => {
      val key = f.get(0).toString
      val value = f.get(1).toString
      val meaning = f.get(2).toString
      (key,value,meaning)
    })
  }

}
