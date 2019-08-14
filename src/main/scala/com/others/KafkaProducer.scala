package com.others

import java.util.{Date, Properties}
import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerConfig, ProducerRecord}
import com.yee.bigdata.common.util.Random


object KafkaProducer {

    def main(args:Array[String]): Unit ={
      val topic = "test1"
      val brokers = "localhost:9092"//"10.7.52.89:9092"

      val props = new Properties()
      props.put("bootstrap.servers",brokers)
      props.put("client.id","0")
      props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer")

      val producer = new KafkaProducer[String,String](props)

      while(true){
        val now = ZtAlgorithmUtil.long2Date(System.currentTimeMillis())
        val id = Random.idToString(Random.randomId())
        val value = id + "|" + now
        val producerRecord = new ProducerRecord[String, String](topic, value)
        producer.send(producerRecord)

        Thread.sleep(100 * 1)
      }

      producer.close()
    }

}
