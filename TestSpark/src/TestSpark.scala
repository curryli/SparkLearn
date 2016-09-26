//输入
//file1   hello world
//file2   world is yours
//file3   hello dog
//file4   xurui hello 

//输出：
//is      file2
//dog     file3
//hello   file1 file3 file4
//yours   file2
//world   file1 file2
//xurui   file4

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.mutable 
 
object Median {
   def main (args: Array[String]) {
     // 屏蔽不必要的日志显示在终端上
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
    
    val conf =new SparkConf().setAppName("Median")
    val sc=new SparkContext(conf)
     
    val data =sc.textFile("xrli/testdata.txt")
    val result = data.mapPartitions( iter => {  
        var tmp = iter.next().toInt  
        while (iter.hasNext) {  
          tmp ^= iter.next().toInt  
        }  
        List((1, tmp)).iterator  
      }).reduceByKey(_ ^ _).collect  
  
      println(result(0))  
   
    
    
    sc.stop()
  }

}