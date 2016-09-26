import org.apache.spark._ 
import SparkContext._ 
object WordCount { 
  def main(args: Array[String]) { 
    val conf = new SparkConf()
    conf.setAppName("WorkCount")
    val sc = new SparkContext(conf)
    
    val textFile = sc.textFile(args(0)) 
    val result = textFile.flatMap(line => line.split("\\s+")) 
        .map(word => (word, 1)).reduceByKey(_ + _).map{case (word, count) => (count, word)}.sortByKey(false) 
    result.saveAsTextFile(args(1)) 
  } 
} 