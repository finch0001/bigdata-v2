package com.yee.bigdata.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.sql.streaming.ProcessingTime
import org.apache.spark.sql.{ForeachWriter, SparkSession}

object ConsumerWriter {
  def main(args:Array[String]): Unit ={

    val spark = SparkSession.builder().appName("structured streaming app")
      .master("local[2]")
      .config("spark.debug.maxToStringFields","100000")
      .getOrCreate()

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "test1")
      .load()

    import spark.implicits._
    val source = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)").as[(String, String)]

    /*
    source.writeStream.format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("topic", "test3")
      .start()
    */

    /*
    source.writeStream.foreach(
      new ForeachWriter[(String, String)] {
        /*
        override def open(partitionId: Long, version: Long): Boolean = {
          // Open connection
          println(s"partitionId:$partitionId version:$version")
          true
        }

        override def process(value: (String, String)): Unit = {
          // Write string to connection
          val key = value._1
          val va  = value._2
          println(s"key:$key value:$va")
        }

        override def close(errorOrNull: Throwable): Unit = {
          // Close the connection
          println("close the connection")
        }
        */

        val kafkaProperties = new Properties()
        kafkaProperties.put("bootstrap.servers", "localhost:9092")
        kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        kafkaProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        val results = new scala.collection.mutable.HashMap[String, String]
        var producer: KafkaProducer[String, String] = _

        def open(partitionId: Long,version: Long): Boolean = {
          producer = new KafkaProducer(kafkaProperties)
          true
        }

        def process(value: (String, String)): Unit = {
          producer.send(new ProducerRecord("test3", value._1 + ":" + value._2))
        }

        def close(errorOrNull: Throwable): Unit = {
          producer.close()
        }

      }
    ).start()
    */


    val writer = new KafkaSink("", "")

    val query = source.writeStream.foreach(writer).outputMode("append").trigger(ProcessingTime("2 seconds"))
    query.start()

    // source.write.m;ode("append").saveAsTable("parquet_external")
  }

}
