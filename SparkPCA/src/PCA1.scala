import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.feature.PCA
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}


object myPCA {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("PCA example")
    val sc = new SparkContext(conf)

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.Server").setLevel(Level.OFF)

    val data = sc.textFile("xrli/pca2.data")
    //data.foreach(println)

    val parseData = data.map{ line =>
      val part = line.split("\\s+")
      Vectors.dense(part.map(_.toDouble))
    }

    val model = new PCA(3).fit(parseData)

    model.transform(parseData).foreach(println)
    //--------------------------------------------------------------------------
    
    println("---------------------------------------------------")
     

  }
}
