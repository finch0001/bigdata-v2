package com.yee.bigdata.common.collection

import it.unimi.dsi.fastutil.Arrays
import it.unimi.dsi.fastutil.BigList
import it.unimi.dsi.fastutil.doubles.Double2CharMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap


object FastUtilTest {


  def foo(): Unit ={
    val foo = new Object2IntOpenHashMap[String]
    foo.put("foo", 1)
    val bar = new Object2IntOpenHashMap[String]
    bar.put("foo", 1)
    bar.put("bar", 1)

    val mapIter = bar.object2IntEntrySet().fastIterator()
    while(mapIter.hasNext()) {
      val x = mapIter.next()
      foo.put(x.getKey(), x.getIntValue() + foo.getOrDefault(x.getKey(), 0))
    }
    println(foo)
  }

  def main(args:Array[String]): Unit ={
    foo()
  }
}
