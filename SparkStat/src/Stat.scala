import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.mllib.stat.test
import org.apache.spark.mllib.regression.LabeledPoint

object StatExample {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Stat example") 
    val sc = new SparkContext(conf)

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.Server").setLevel(Level.OFF)
    
    //读取数据，转换成RDD[Vector]类型
    val data_path = "xrli/sample_stat.txt"
    val data = sc.textFile(data_path).map(_.split("\\s")).map(f => f.map(f => f.toDouble))
    val data1 = data.map(f => Vectors.dense(f))   
    //计算每列最大值、最小值、平均值、方差值、L1范数、L2范数
    
    val stat1 = Statistics.colStats(data1)
    println("max is " + stat1.max)
    println("min is " + stat1.min)
    println("mean is " + stat1.mean)
    println("variance is " + stat1.variance)
    println("normL1 is " + stat1.normL1)
    println("normL2 is " + stat1.normL2)
    println
    
   //计算pearson系数、spearman相关系数
    val corr1 = Statistics.corr(data1, "pearson")
    val corr2 = Statistics.corr(data1, "spearman")
    val x1 = sc.parallelize(Array(1.0, 2.0, 3.0, 4.0))
    val y1 = sc.parallelize(Array(5.0, 6.0, 6.0, 6.0))
    val corr3 = Statistics.corr(x1, y1, "pearson")
    println("corr1 is " + corr1)
    println("corr2 is " + corr2)
    println("corr3 is " + corr3)
    println
    
    val v1 = Vectors.dense(43.0, 9.0)
    val v2 = Vectors.dense(44.0, 4.0)   
    val c1 = Statistics.chiSqTest(v1, v2)
    println("c1 is " + c1)
    println
    
     // Load and parse the data
    val dataLR = sc.textFile("xrli/lpsa.data")
    
    val parsedData = dataLR.map { line =>
      val y = line.split(',')(0).toDouble
      val x = line.split(',')(1).split(' ').map(_.toDouble)
      LabeledPoint(y, Vectors.dense(x))
    }.cache
    
    val c2 = Statistics.chiSqTest(parsedData)
    println("c2 is an RDD Array")
    c2.foreach(println)
    
    sc.stop()
  }
}
// scalastyle:on println