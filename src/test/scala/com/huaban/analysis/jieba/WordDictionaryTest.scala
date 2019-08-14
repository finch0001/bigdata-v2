package com.huaban.analysis.jieba

object WordDictionaryTest {

  def main(args:Array[String]): Unit ={
    val wordDictionary = WordDictionary.getInstance()
    wordDictionary.loadDict()
    val freq = wordDictionary.getFreq("安全门")
    println("freq of 安全门 is:" + freq)
  }
}
