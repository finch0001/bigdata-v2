package com.yee.bigdata.common.util

import java.util.Date
import java.util.TimeZone

object DateTimeUtilsTest {

  def main(args:Array[String]): Unit ={
    val dateTime = DateTimeUtils.formatTimestamp(new Date(),TimeZone.getDefault)
    println(dateTime)
  }
}
