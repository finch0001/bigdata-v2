package com.yee.bigdata.spark

import org.apache.spark.sql.SparkSession

object HiveQuery {


  def main(args:Array[String]): Unit ={
    val availableCores = Runtime.getRuntime.availableProcessors()
    val master = s"local[$availableCores]"
    val sparkSession = SparkSession.builder.appName("CarMileage")
      .master(master)
      //.config("spark.sql.warehouse.dir", "/user/hive/warehouse/")
      .config("spark.debug.maxToStringFields","10000000")
      .enableHiveSupport
      .getOrCreate

    sparkSession.sparkContext.setLogLevel("WARN")

    // val hql = "select * from ys_test_07 where vin = 'ay75re9lboikj93yf6so1' order by te asc"
    // val hql = "select count(1) from ys_test_07 where vin = 'ay75re9lboikj93yf6so1'"
    // val hql = "select * from ys_test_07 limit 10"
    // val hql = "select max(te),min(te) from ys_test_02"
    // val hql = "select count(distinct(vin)) from ys_test_02"
    // val hql = "select count(distinct(vin)) from ys_test_02 where te >= '2019-06-12 15:11:56' and te <= '2019-06-13 01:11:56'"
    // val hql = "select max(te),min(te) from ys_test_006"
    // val hql = "select count(1) from ys_test_001 where vin = 'ioo2q6'"
    // val hql = "select count(distinct(vin)) from ys_test_006"
    // val hql = "select count(1) from ys_test_006 where vin = 'aqympq'"

    // sparkSession.sql(hql).show()


    val startTime = System.currentTimeMillis()
    val df = sparkSession.read.parquet("hdfs://namenode:8020/down/data/rabbmitmq_20190624/*")
    //df.printSchema()


    val rdd = df.rdd.map(f => {
      val vin = f.getAs("vin").toString
      val te = f.getAs("te").toString
      val tc = f.getAs("tc").toString
      val md = f.getAs("md").toString
      (vin,te,tc,md)
    })


    /*
    val rdd = df.rdd.mapPartitions(f => {
      f.map(f => {
        val vin = f.getAs("vin").toString
        val te = f.getAs("te").toString
        val tc = f.getAs("tc").toString
        val md = f.getAs("md").toString
        (vin,te,tc,md)
      })
    })
    */

    println("rdd partition num: " + rdd.getNumPartitions)

    rdd.saveAsTextFile("file:///E:\\out\\res4\\5")
    val stopTime = System.currentTimeMillis()
    println("time cost: " + (stopTime - startTime))

    //df.createOrReplaceTempView("test")
    //sparkSession.sql("select count(*) from test").show()

    sparkSession.stop()
  }

}
