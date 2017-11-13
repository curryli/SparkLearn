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

object card_machnt_test {
 
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
    
    
    var ratings = ss.read.option("header", true).format("csv").option("inferSchema","true").load("xrli/credit/card_mchnt_map.csv")
    ratings.dtypes.foreach(println)
    ratings.show
  
    var indexer1 = new StringIndexer().setInputCol("card_no").setOutputCol("card_no_idx").setHandleInvalid("skip")
    ratings = indexer1.fit(ratings).transform(ratings)
//        
    var indexer2 = new StringIndexer().setInputCol("mchnt_cd").setOutputCol("mchnt_cd_idx").setHandleInvalid("skip")
    ratings = indexer2.fit(ratings).transform(ratings)
    ratings.dtypes.foreach(println) 
//    
//    ratings.show
  
    val alsImplicit = new ALS().setMaxIter(5).setRegParam(0.01).setImplicitPrefs(true). setUserCol("card_no_idx").setItemCol("mchnt_cd_idx").setRatingCol("rating")
     
    val modelImplicit = alsImplicit.fit(ratings)
    
    
    
    val card_df_idx = ratings.select("card_no_idx").distinct()
    //println(card_df_idx.count)
    
    val mchnt_df_pairs = ratings.select("mchnt_cd", "mchnt_cd_idx").distinct()
    val card_df_pairs = ratings.select("card_no", "card_no_idx").distinct()
 
    card_df_pairs.show
    mchnt_df_pairs.show 
    
    //val test_mchnt_rdd = sc.textFile("xrli/credit/test_mchnt.csv").map(x => Row(x))
    val test_mchnt_rdd = sc.textFile("xrli/credit/mchnt_2.csv").map(x => Row(x))
    
    val schema =  new StructType().add("mchnt_cd",StringType,true)
    
    var test_mchnt = ss.createDataFrame(test_mchnt_rdd,schema)
    //test_mchnt.show
    
    val test_mchntlist = test_mchnt.rdd.map(r=>r.getString(0)).collect()
    //test_mchntlist.foreach(println)
    
    
    
    val test_mchnt_idx = mchnt_df_pairs.filter(mchnt_df_pairs("mchnt_cd").isin(test_mchntlist:_*))
    //print(test_mchnt_idx.count)
    //test_mchnt_idx.show
    
    val test_mchnt_df = test_mchnt_idx.select("mchnt_cd_idx").distinct()
    
    println("card_df_idx: " , card_df_idx.count)
    
    println("test_mchnt_df: " , test_mchnt_df.count)
    val cross_table =  card_df_idx.crossJoin(test_mchnt_df)
    println("cross_table count: " , cross_table.count)
    
    //cross_table.show
    
    
    
    var predictionsImplicit = modelImplicit.transform(cross_table)
    
    //predictionsImplicit.show()
    
    println(predictionsImplicit.count())
  
    println("Ranking......")
    
    //predictionsImplicit.show
 /////////////////////////////////////////////////////////////////////   
    
    //val win = Window.partitionBy("mchnt_cd_idx").orderBy("prediction")   //分组topN   根据用户分组，对该组内的评分排序
    //predictionsImplicit = predictionsImplicit.withColumn("ranks", rank().over(win))  
    
    //predictionsImplicit = predictionsImplicit.withColumn("ranks", row_number.over(Window.partitionBy("mchnt_cd_idx").orderBy("prediction")))  
    
    //predictionsImplicit = predictionsImplicit.withColumn("ranks", row_number.over(Window.partitionBy("mchnt_cd_idx").orderBy(col("prediction").desc)))  
    
    predictionsImplicit = predictionsImplicit.withColumn("ranks", row_number.over(Window.partitionBy("mchnt_cd_idx").orderBy(desc("prediction"))))
    
    //predictionsImplicit.show(500)
    
    predictionsImplicit = predictionsImplicit.filter(predictionsImplicit("ranks")<100)
    //predictionsImplicit.show(500)
    
    
    
    predictionsImplicit = predictionsImplicit.join(mchnt_df_pairs, predictionsImplicit("mchnt_cd_idx")===mchnt_df_pairs("mchnt_cd_idx"), "left_outer")
    predictionsImplicit = predictionsImplicit.join(card_df_pairs, predictionsImplicit("card_no_idx")===card_df_pairs("card_no_idx"), "left_outer")
    
    println("Joined table:\n")
    predictionsImplicit.show
    
    val cols = Array("mchnt_cd", "card_no", "prediction")
    
    predictionsImplicit.selectExpr(cols:_*).rdd.map(_.mkString(",")).saveAsTextFile("xrli/credit/CF_rank100_2") 
      
    println("All done in " + (System.currentTimeMillis()-startTime)/(1000*60) + " minutes." )   
   
     
  }
  
  
  
    
}