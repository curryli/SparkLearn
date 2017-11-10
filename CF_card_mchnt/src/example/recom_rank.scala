package example
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark._
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.SparkContext._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.graphx._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext
import scala.collection.mutable.MutableList
import scala.Range
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import scala.collection.mutable.{Buffer,Set,Map}
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions
import org.apache.spark.sql.functions._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.ml.feature.StandardScaler
import org.apache.spark.ml.feature.MinMaxScaler
import org.apache.spark.ml.feature.Normalizer
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.feature.OneHotEncoder
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification._
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorAssembler}
import scala.collection.mutable.ArrayBuffer
import scala.reflect.runtime.universe
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.PipelineStage
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.feature.QuantileDiscretizer
import scala.collection.mutable.HashMap
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.functions 


object recom_rank {
 
  def main(args: Array[String]): Unit = {

    //屏蔽日志
    Logger.getLogger("org").setLevel(Level.ERROR);
    Logger.getLogger("akka").setLevel(Level.ERROR);
    Logger.getLogger("hive").setLevel(Level.WARN);
    Logger.getLogger("parse").setLevel(Level.ERROR); 
    
    //val sparkConf = new SparkConf().setAppName("spark2SQL")
    val warehouseLocation = "spark-warehouse"
    
    val ss = SparkSession
      .builder()
      .appName("Save_IndexerPipeLine")
      .config("spark.sql.warehouse.dir", warehouseLocation)
      .config("hive.metastore.schema.verification", false)
      .getOrCreate()
      
    val sc = ss.sparkContext
  
    import ss.implicits._
    
    val startTime = System.currentTimeMillis(); 
    
    
    val data = sc.textFile("xrli/credit/CF_results_new").map{
       str=>
           var tmparr = str.split(",")         
           var tmpList = List(tmparr(0).toString()).:+(tmparr(1).toString).:+(tmparr(2).toDouble)
           
           Row.fromSeq(tmparr.toSeq)
    }
      
    val schema =  new StructType().add("mchnt_cd",StringType,true).add("card_no",StringType,true).add("prediction",DoubleType,true)
    
    var test_mchnt = ss.createDataFrame(data,schema)
    test_mchnt.show
    
    val win = Window.partitionBy("mchnt_cd").orderBy("prediction")
    
    test_mchnt = test_mchnt.withColumn("ranks", rank().over(win))
    test_mchnt.show(500)
    
    test_mchnt = test_mchnt.filter(test_mchnt("ranks")<20)
    test_mchnt.show(500)
    
    
//    val newdata = test_mchnt.select("*, rank() over(partition by mchnt_cd order by prediction) as ranks")
//    newdata.show 
     
 
    println("All done in " + (System.currentTimeMillis()-startTime)/(1000*60) + " minutes." )   
   
     
  }
  
  
  
    
}