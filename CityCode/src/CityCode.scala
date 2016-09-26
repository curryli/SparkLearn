import org.apache.spark._ 
import SparkContext._ 

object CityCode { 
  def main(args: Array[String]) { 
    val conf = new SparkConf()
    conf.setAppName("CityCode")
    val sc = new SparkContext(conf)
    
    val textFile = sc.textFile("xrli/citycode") 
    val sp = textFile.map{ line =>
      val fields = line.split("\\t")
     (fields(0),fields(1),fields(2),fields(3))
    }
      
    
    //val city = sp.filter(x => x._1 == x._3 || x._3=="NA").flatMap(x => Array(x._1,x._2))
     
    val city = sp.filter(x => x._1 == x._3 || x._3=="NA").map(x => (x._2,x._1))
    city.saveAsTextFile("xrli/city") 
  } 
} 