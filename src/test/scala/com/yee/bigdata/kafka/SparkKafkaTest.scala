package com.yee.bigdata.kafka

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.{ProcessingTime, Trigger}
import scala.concurrent.duration._

object SparkKafkaTest {

  def main(args:Array[String]): Unit ={
    val spark = SparkSession.builder().appName("structured streaming app")
      .master("local[2]")
      .config("spark.debug.maxToStringFields","100000")
      .getOrCreate()


    spark.sparkContext.setLogLevel("error")
    import spark.implicits._

    // Subscribe to 1 topic
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "test1")
      .load()

    val source = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)").as[(String, String)]

    /*
    source.writeStream.format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("topic","test2")
      .start()
    */
    //source.writeStream.format("text").option("checkpointLocation","E:\\out\\res5\\checkpoint1").start("E:\\out\\res5\\1")

    /*
    val outstream = source.writeStream
      .outputMode("append")
      .format("parquet")
      .option("path", "E:\\\\out\\\\res6\\kafka-test.out")
      .option("checkpointLocation","E:\\\\out\\\\res6\\checkpoint")
      .trigger(ProcessingTime(300.seconds))
      .start()
    */

    // outstream.awaitTermination()

    source.writeStream.outputMode("append").format("memory").queryName("result").start()


  }
}
