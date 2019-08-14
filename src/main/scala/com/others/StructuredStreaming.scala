package com.others

import org.apache.spark.sql.SparkSession

object StructuredStreaming {

  def main(args:Array[String]): Unit ={
    val sparkSession = SparkSession.builder().appName("structured streaming app")
      .master("local[2]").getOrCreate()

    import sparkSession.implicits._

    val lines = sparkSession.readStream.format("socket")
      .option("host","192.168.2.111")
      .option("port","9999")
      .load()

    val words = lines.as[String].flatMap(_.split(" "))

    val wordCounts=words.groupBy("value").count()

    val query = wordCounts.writeStream
      .outputMode("complete")
      .format("console")
      .start()

    query.awaitTermination()
  }

}
