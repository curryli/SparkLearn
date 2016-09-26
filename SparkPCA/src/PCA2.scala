import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.feature.PCA
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.Matrix
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix


object myPCA2 {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("PCA example") 
    val sc = new SparkContext(conf)

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.Server").setLevel(Level.OFF)


    val data = sc.textFile("xrli/pca2.data")
    val parsedata = data.map(_.split("\\s+"))     //转换为RDD[Array[String]]类型  
                         .map(_.map(_.toDouble))  //转换为RDD[Array[Double]]类型 
                           .map(Vectors.dense(_))  //转换为RDD[Vector]类型 
                                  
    val mat: RowMatrix = new RowMatrix(parsedata)

    // Compute the top 3 principal components.
    // Principal components are stored in a local dense matrix.
    val pc: Matrix = mat.computePrincipalComponents(3)
    println(pc)
    
    // Project the rows to the linear space spanned by the top 3 principal components.
    val projected: RowMatrix = mat.multiply(pc)  //坐标系转换+维度提炼
    //--------------------------------------------------------------------------
    
    println("---------------------------------------------------")
     

  }
}
