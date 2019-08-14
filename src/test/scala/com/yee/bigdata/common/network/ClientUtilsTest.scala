package com.yee.bigdata.common.network


import java.nio.file.{Path, Paths}
import java.sql.Date

import scala.collection.JavaConversions._
import com.yee.bigdata.common.util.ClientUtils
import java.util.ArrayList

import com.yee.bigdata.common.cache.LRUCache

object ClientUtilsTest {
  def main(args:Array[String]): Unit ={
    //val hostPortStr = "www.baudu.com:9091"
    //val host = ClientUtils.getHost(hostPortStr)
    //val port = ClientUtils.getPort(hostPortStr)
    //println("host:" + host + " port:" + port)

    //val list = new ArrayList[String]()
    //list.add("www.souhu.com:9081")
    //list.add("81.09.12.11:82")

    //val address = ClientUtils.parseAndValidateAddresses(list)
    //address.toList.foreach(f => {
    //  println(f.toString)
    //})

    /*
    val data = new ArrayList[String]()
    data.add("a")
    data.add("4")
    data.add("t")
    data.add("o")

    val restult = ClientUtils.sorted(data)
    restult.toList.foreach(println)
    */

    //val count = ClientUtils.utf8Length("芬奇你还好吗?")
    //print("count:" + count)

    //val clazz = ClientUtils.newInstance(Class.forName("com.yee.bigdata.common.util.Random"))
    //val random = clazz.asInstanceOf[com.yee.bigdata.common.util.Random]
    //val clazz = ClientUtils.newInstance("com.yee.bigdata.common.util.Random",Class.forName("com.yee.bigdata.common.util.Random"))
    //val random = clazz.asInstanceOf[com.yee.bigdata.common.util.Random]
    //println(random.toString)
    val source = "C:\\Users\\Administrator\\Desktop\\cap\\005.txt"
    val dest = "C:\\Users\\Administrator\\Desktop\\cap\\a\\005.txt"
    ClientUtils.atomicMoveWithFallback(Paths.get(source),Paths.get(dest))
  }

}
