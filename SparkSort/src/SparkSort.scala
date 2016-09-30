import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
 
object Sort{
  def main(args: Array[String]) {
    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
 
    //设置运行环境
    val conf = new SparkConf().setAppName("SparkSort") 
    val sc = new SparkContext(conf)
 
      // 装载数据集
    val data = sc.textFile("xrli/AmountDedup/*")
    val amountList = data.map(_.toDouble).collect 
    
     
    val amountSorted =  sc.makeRDD(amountList.sorted)
    
    //val amountSorted =   sc.makeRDD(amountList.sortBy(x=>x))
    
    amountSorted.saveAsTextFile("xrli/SortOut")
 
    sc.stop()
  }
}