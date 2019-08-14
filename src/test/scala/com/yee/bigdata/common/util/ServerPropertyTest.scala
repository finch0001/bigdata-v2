package com.yee.bigdata.common.util

object ServerPropertyTest {


  def main(args:Array[String]): Unit ={
    val propsPath = "E:\\work\\project\\learn\\bigdata\\src\\main\\resources\\config.properties"
    val props = ServerProperty.loadProps(propsPath)
    val propsMap = ServerProperty.propsToStringMap(props)
    println(propsMap)

  }
}
