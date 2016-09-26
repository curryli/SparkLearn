import org.apache.spark._ 
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import SparkContext._ 
import org.apache.log4j.{Level, Logger}

object SparkJSON { 
  def main(args: Array[String]) { 
    
     //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
    
    val sparkConf = new SparkConf().setAppName("customers")
    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)
    
    
     // A JSON dataset is pointed to by path.
     // The path can be either a single text file or a directory storing text files.
    val path = "xrli/people.json"
    // Create a SchemaRDD from the file(s) pointed to by path
    val people = sqlContext.jsonFile(path)

    // The inferred schema can be visualized using the printSchema() method.
    people.printSchema()
// root
//  |-- age: integer (nullable = true)
//  |-- name: string (nullable = true)

    // Register this SchemaRDD as a table.
    people.registerTempTable("people")

    // SQL statements can be run by using the sql methods provided by sqlContext.
    val teenagers = sqlContext.sql("SELECT name FROM people WHERE age >= 13 AND age <= 19")

// Alternatively, a SchemaRDD can be created for a JSON dataset represented by
// an RDD[String] storing one JSON object per string.
    val anotherPeopleRDD = sc.parallelize(
      """{"name":"Yin","address":{"city":"Columbus","state":"Ohio"}}""" :: Nil)
    val anotherPeople = sqlContext.jsonRDD(anotherPeopleRDD)
    
    anotherPeople.printSchema()
    anotherPeople.registerTempTable("anotherPeople")
    
    sqlContext.sql("SELECT name FROM anotherPeople")
  } 
} 