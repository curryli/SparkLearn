import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.feature.PCA
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.Matrix
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix


object myPCA4 {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("PCA example") 
    val sc = new SparkContext(conf)

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.Server").setLevel(Level.OFF)

    val data = Array(
    Vectors.sparse(5, Seq((1, 1.0), (3, 7.0))),
    Vectors.dense(2.0, 0.0, 3.0, 4.0, 5.0),
    Vectors.dense(4.0, 0.0, 0.0, 6.0, 7.0))

    val dataRDD = sc.parallelize(data, 2)

    val mat: RowMatrix = new RowMatrix(dataRDD)

    // Compute the top 3 principal components.
    // Principal components are stored in a local dense matrix.
    val pc: Matrix = mat.computePrincipalComponents(3)

    // Project the rows to the linear space spanned by the top 3 principal components.
    val projected: RowMatrix = mat.multiply(pc)  //坐标系转换+维度提炼
    //--------------------------------------------------------------------------
    
    println("---------------------------------------------------")
     

  }
}
