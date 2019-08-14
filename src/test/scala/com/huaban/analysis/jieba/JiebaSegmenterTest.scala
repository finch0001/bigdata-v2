package com.huaban.analysis.jieba

import com.huaban.analysis.jieba.JiebaSegmenter
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode
import org.junit.Test

object JiebaSegmenterTest {

  def testDemo(): Unit = {
    val segmenter = new JiebaSegmenter
    val sentences = Array[String]("这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。", "我不喜欢日本和服。", "雷猴回归人间。", "工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作", "结果婚的和尚未结过婚的")
    for (sentence <- sentences) {
      System.out.println(segmenter.process(sentence, SegMode.INDEX).toString)
    }
  }

  def testCreateDAG(): Unit ={
     val segmenter = new JiebaSegmenter
     val sentence = "这3种材料是显示面板及半导体芯片制造过程当中所需的关键材料"//"这是一个伸手不见五指的黑夜"

     val dag = segmenter.createDAG(sentence)
     println("dag:" + dag)
  }

  def main(args:Array[String]): Unit ={
    testDemo()
    // testCreateDAG()
  }


}
