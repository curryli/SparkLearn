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


object cf_test {
 
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
    
    
    val ratings = ss.read.option("header", true).format("csv").option("inferSchema","true").load("xrli/credit/ratings.csv")
    ratings.dtypes.foreach(println)
    ratings.show
    
    //val ratings = sc.textFile("xrli/credit/ratings.csv").map(parseRating).toDF()
 
    val Array(training, test) = ratings.randomSplit(Array(0.8, 0.2))
    
    val alsExplicit = new ALS().setMaxIter(5).setRegParam(0.01).setUserCol("userId"). setItemCol("movieId").setRatingCol("rating")
    
    val alsImplicit = new ALS().setMaxIter(5).setRegParam(0.01).setImplicitPrefs(true). setUserCol("userId").setItemCol("movieId").setRatingCol("rating")
    
    val modelExplicit = alsExplicit.fit(training)
    
    val modelImplicit = alsImplicit.fit(training)
    
    val predictionsExplicit = modelExplicit.transform(test)
    val predictionsImplicit = modelImplicit.transform(test)
    
    predictionsExplicit.show()
    predictionsImplicit.show()
    
    val evaluator = new RegressionEvaluator().setMetricName("rmse").setLabelCol("rating"). setPredictionCol("prediction")
    val rmseExplicit = evaluator.evaluate(predictionsExplicit)
    val rmseImplicit = evaluator.evaluate(predictionsImplicit)
    
    println(s"Explicit:Root-mean-square error = $rmseExplicit")
    println(s"Implicit:Root-mean-square error = $rmseImplicit")
    
    
    
     
     
 
    println("All done in " + (System.currentTimeMillis()-startTime)/(1000*60) + " minutes." )   
   
     
  }
  
  
  
    
}