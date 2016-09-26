import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.StreamingContext._
import org.apache.log4j.{Level, Logger}
 
object FileWordCount {
  def main(args: Array[String]) {
    
     // 屏蔽不必要的日志显示终端上
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
    
    val sparkConf = new SparkConf().setAppName("FileWordCount")   //.setMaster("local[2]")
 
    // 创建Streaming的上下文，包括Spark的配置和时间间隔，这里时间为间隔10秒
    val ssc = new StreamingContext(sparkConf, Seconds(10))
 
    // 指定监控的目录，在这里为hadoop上的xrli/Stream/FileWordCountTemp
    val lines = ssc.textFileStream("xrli/Stream/FileWordCountTemp")
 
    // 对指定文件夹变化的数据进行单词统计并且打印
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
    wordCounts.print()
 
       // 启动Streaming
    ssc.start()
    ssc.awaitTermination()
     
  }
}