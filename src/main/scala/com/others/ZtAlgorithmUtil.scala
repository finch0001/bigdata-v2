package com.others

import java.text.SimpleDateFormat
import java.util.{Calendar, Date, Properties}
import scala.collection.mutable.ArrayBuffer

object ZtAlgorithmUtil {

  /**
    *  方法功能: 将指定的时间范围按天拆分
    *  @param startTime 需要拆分的开始时间  yyyy-MM-dd HH:mm:ss
    *  @param endTime   需要拆分的结束时间  yyyy-MM-dd HH:mm:ss
    *  @return daysArr  拆分后的日期,开始结束时间,字符|分隔
    * */
  def timeRangeSplitByDay(startTime:String,endTime:String): ArrayBuffer[String] ={
    // 拆分后的时间,使用|分隔
    val daysArr = ArrayBuffer[String]()
    val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val calendar = Calendar.getInstance()

    val startDate = parseToDate(startTime)
    val endDate = parseToDate(endTime)

    calendar.setTime(startDate)
    val startTimeMillis = calendar.getTimeInMillis
    calendar.setTime(endDate)
    val endTimeMillis = calendar.getTimeInMillis

    var currTimeMillis = startTimeMillis
    while(currTimeMillis <= endTimeMillis){
      val splitStartMillis = currTimeMillis

      calendar.setTimeInMillis(splitStartMillis)
      var year = calendar.get(Calendar.YEAR)
      var month = calendar.get(Calendar.MONTH) + 1
      var day = calendar.get(Calendar.DAY_OF_MONTH)
      var hour = calendar.get(Calendar.HOUR_OF_DAY)
      var minute = calendar.get(Calendar.MINUTE)
      var seconds = calendar.get(Calendar.SECOND)

      // 日初
      val splitedStart = sdf.format(calendar.getTime)

      // 当前时间设置为日末
      calendar.set(Calendar.HOUR_OF_DAY,23)
      calendar.set(Calendar.MINUTE,59)
      calendar.set(Calendar.SECOND,59)

      year = calendar.get(Calendar.YEAR)
      month = calendar.get(Calendar.MONTH) + 1
      day = calendar.get(Calendar.DAY_OF_MONTH)
      hour = calendar.get(Calendar.HOUR_OF_DAY)
      minute = calendar.get(Calendar.MINUTE)
      seconds = calendar.get(Calendar.SECOND)

      var splitedEnd = ""
      if(calendar.getTimeInMillis >= endTimeMillis){
        splitedEnd = endTime
      }else {
        splitedEnd = sdf.format(calendar.getTime)
      }

      // 拆分后的一天,开始结束使用字符|分隔
      val currDay = splitedStart + "|" + splitedEnd
      daysArr += currDay

      // 新增一天
      calendar.add(Calendar.DAY_OF_MONTH,1)
      // 设置日初
      calendar.set(Calendar.HOUR_OF_DAY,0)
      calendar.set(Calendar.MINUTE,0)
      calendar.set(Calendar.SECOND,0)

      currTimeMillis = calendar.getTimeInMillis
    }

    daysArr
  }

  /**
    *  方法功能: 将yyyy-MM-ddTHH:mm:ssZ格式的日期时间转换成yyyy-MM-dd HH:mm:ss格式的日期时间
    *  @param te 待转换的字符串日期时间
    *  @return 转换后的字符串日期时间
    * */
  def teConvert(te:String): String ={
    //  转换成long型时间戳
    val teLong = dateTime2Long(te)
    // 转成yyyy-MM-dd HH:mm:ss格式日期时间
    long2Date(teLong)
  }

  /**
    *  方法功能: 字符串格式的时间戳值转换为yyyy-MM-dd HH:mm:ss格式的字符串日期
    *  @param timeStampLong long格式的时间戳
    *  @return date yyyyMMdd格式的字符串日期
    * */
  def long2Date(timeStampLong:Long): String ={
    val simpleDateFormat = new SimpleDateFormat()
    val date = new Date(timeStampLong)
    simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss")
    simpleDateFormat.format(date)
  }

  /**
    *  方法功能: 将给定字符串时间值转换为long型时间
    *  @param te 需要转换的时间 yyyy-MM-dd HH:mm:ss
    *  @return teLong
    */
  def dateTime2Long(te: String): Long = {
    // Long型的结果时间值
    var teLong: Long = 0

    val date = parseToDate(te)

    if(date != null){
      val c = Calendar.getInstance
      c.setTime(date)
      if(te.contains("T")){
        c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + 8)
      }
      teLong = c.getTimeInMillis
    }

    teLong
  }


  /**
    *  方法功能: 将字符串时间转成java日期Date
    *  @param te 字符串时间值
    *  @return date Date格式时间
    */
  def parseToDate(te: String): Date = {
    var date: Date = null
    val sdf = new SimpleDateFormat()

    if(te.contains("T")){
      try{
        sdf.applyPattern("yyyy-MM-dd'T'HH:mm:ss")
        date = sdf.parse(te)
      }catch{
        case t: Throwable => {
          sdf.applyPattern("yyyy/MM/dd'T'HH:mm:ss")
          try {
            date = sdf.parse(te)
          }catch{
            case ex:Throwable => { /*ex.printStackTrace()*/ }
          }
        }
      }
    }else{
      try{
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss")
        date = sdf.parse(te)
      }catch{
        case t: Throwable => {
          sdf.applyPattern("yyyy/MM/dd HH:mm:ss")
          try {
            date = sdf.parse(te)
          }catch{
            case ex: Throwable => { /*ex.printStackTrace()*/ }
          }
        }
      }
    }

    date
  }

  /**
    * 功能描述: <br>
    * 〈功能详细描述〉校验正确的日期格式
    *
    * @param str
    * @param format
    * @return boolean
    * @see [相关类/方法](可选)
    * @since [产品/模块版本](可选)
    */
  def isValidDate(str: String, format: String): Boolean = {
    var convertSuccess = true
    // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
    val sf = new SimpleDateFormat(format)
    try {
      // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
      sf.setLenient(false)
      sf.parse(str)
    } catch {
      case e: Throwable => {
        // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
        convertSuccess = false
      }
    }

    convertSuccess
  }

  /**
    *  方法功能: 判断里程值是否有效值
    *  @param mile 需要判断的里程值
    *  @return isValid 里程值是否有效
    *
    * */
  def isValidMile(mile:String): Boolean ={
    var isValid = true

    isValid = if(mile.isEmpty || mile.trim.toLowerCase.equals("null")) false else true

    try{
      val mileDouble = mile.toDouble
      isValid = if(mileDouble > 0) true else false
    }catch{
      case ex:Throwable => { isValid = false }
    }

    isValid
  }

  /**
    *  方法功能: 判断值油耗是否有效值
    *  @param oil 需要判断的油耗值
    *  @return isValid 油耗值是否有效
    *
    * */
  def isValidOil(oil:String): Boolean ={
    var isValid = true

    isValid = if(oil.isEmpty || oil.trim.toLowerCase.equals("null")) false else true

    try{
      val oilDouble = oil.toDouble
      isValid = if(oilDouble > 0) true else false
    }catch{
      case ex:Throwable => { isValid = false }
    }

    isValid
  }

  /**
    *  方法功能: 判断经度值是否有效值
    *  @param longitude 需要判断的经度值
    *  @return isValid 经度值是否有效
    *
    * */
  def isValidLongitude(longitude:String): Boolean ={
    var isValid = true

    isValid = if(longitude.isEmpty || longitude.trim.toLowerCase.equals("null")) false else true

    try{
      val longitudeDouble = longitude.toDouble
      isValid = if(longitudeDouble > 0) true else false
    }catch{
      case ex:Throwable => { isValid = false }
    }

    isValid
  }

  /**
    *  方法功能: 判断纬度值是否有效值
    *  @param latitude 需要判断的纬度值
    *  @return isValid 纬度值是否有效
    *
    * */
  def isValidLatitude(latitude:String): Boolean ={
    var isValid = true

    isValid = if(latitude.isEmpty || latitude.trim.toLowerCase.equals("null")) false else true

    try{
      val latitudeDouble = latitude.toDouble
      isValid = if(latitudeDouble > 0) true else false
    }catch{
      case ex:Throwable => { isValid = false }
    }

    isValid
  }

  /**
    *  方法功能: 判断车速是否有效值
    *  @param speed 需要判断的车速值
    *  @return isValid 车速值是否有效
    *
    * */
  def isValidSpeed(speed:String): Boolean ={
    var isValid = true

    isValid = if(speed.isEmpty || speed.trim.toLowerCase.equals("null")) false else true

    try{
      val speedDouble = speed.toDouble
      isValid = if(speedDouble > 0) true else false
    }catch{
      case ex:Throwable => { isValid = false }
    }

    isValid
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

  /**
    * 判断算子的输入参数的分段值是否有效
    * @param sub 输入的分段值
    * */
  def assertValidSub(sub:String): Unit ={
    assert(!sub.isEmpty)
  }

  /**
    * 方法功能:加载配置文件
    * @return props 配置属性
    * */
  def loadConfig(): Properties ={
    val props = new Properties()
    val configStream = ZtAlgorithmUtil.getClass.getClassLoader.getResourceAsStream("zt_config.properties")
    props.load(configStream)
    props
  }

  def main(args:Array[String]): Unit ={
    // println("is valid date:" + isValidDate("2019-02-11 21:20:02","yyyy-MM-dd HH:mm:ss"))

    // println("is valid mile:" + isValidMile("+0.000000000000000000003"))
    // val d1:Double = 24044.375
    // val d2:Double = 23846.81
    // println(doubleFormat(d1 - d2))
    // println(teConvert("2016-01-19T04:38:40Z"))
    // println(teConvert("2016-01-19T04:38:40"))
    // println(teConvert("2016-01-19 04:38:40Z"))
    // println(teConvert("2016-01-19 04:38:40"))
    // println(teConvert("2016-01-19t04:38:40Z"))
    // println(teConvert("2016-01-19T04:38:40z"))

    val props = loadConfig()
    val p1 = props.getProperty("zt.car.speed.jump.threshold").toDouble
    val p2 = props.getProperty("zt.oil.jump.threshold").toDouble
    val p3 = props.getProperty("zt.oil.jump.del.threshold").toDouble
    println(s"p1:$p1 p2:$p2 p3:$p3")
  }

}
