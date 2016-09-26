import org.apache.spark._ 
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import SparkContext._ 

object SparkSQLHbase { 
  def main(args: Array[String]) { 
    
    val sparkConf = new SparkConf().setAppName("SQLHbase")
    val sc = new SparkContext(sparkConf)

    val sqlContext = new SQLContext(sc)
      var hbasetable = sqlContext.read.format("com.lxw1234.sparksql.hbase").options(Map("sparksql_table_schema" -> "(row_key string, c1 string, c2 string, c3 string)",
"hbase_table_name" -> "lxw1234", "hbase_table_schema" -> "(:key , f1:c2 , f2:c2 , f3:c3 )"
)).load()
     
    hbasetable.printSchema()
    hbasetable.registerTempTable("lxw1234")
    sqlContext.sql("SELECT * from lxw1234").collect.foreach(println)
    sqlContext.sql("SELECT row_key,concat(c1,'|',c2,'|',c3) from lxw1234").collect.foreach(println)
  } 
} 