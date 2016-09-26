import org.apache.spark._ 
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import SparkContext._ 
import org.apache.log4j.{Level, Logger}

object SparkSQL { 
  def main(args: Array[String]) { 
    //case class Customer(name:String,age:Int,gender:String,address: String)
    
    
     //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
    
    val sparkConf = new SparkConf().setAppName("customers")
    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)
    
    // The schema is encoded in a string
    val schemaString = "name age"

    // Generate the schema based on the string of schema
    val schema =
      StructType(
        schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))

    val people = sc.textFile(args(0))
    
    // Convert records of the RDD (people) to Rows.
    val rowRDD = people.map(_.split(",")).map(p => Row(p(0), p(1).trim))
    
    val dataFrame = sqlContext.createDataFrame(rowRDD, schema)
    dataFrame.printSchema

    dataFrame.registerTempTable("people")
    sqlContext.sql("select * from people where age <25").collect.foreach(println)
  } 
} 