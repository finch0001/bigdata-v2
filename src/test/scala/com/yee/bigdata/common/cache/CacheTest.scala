package com.yee.bigdata.common.cache

object CacheTest {
  def main(args:Array[String]): Unit ={
    val cache = new LRUCache[String,Int](3)
    cache.put("a",10)
    cache.put("b",11)
    cache.put("c",12)
    cache.put("d",13)
    println(cache.get("b"))

  }
}
