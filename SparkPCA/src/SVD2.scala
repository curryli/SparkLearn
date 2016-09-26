import org.apache.spark.mllib.linalg.Matrix
import org.apache.spark.mllib.linalg.SingularValueDecomposition
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}


object mysvd2 {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("PCA example") 
    val sc = new SparkContext(conf)

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.Server").setLevel(Level.OFF)
    
    val mat = new RowMatrix(sc.textFile("xrli/svdM.txt").map(_.split("\\s+"))
                                                 .map(_.map(_.toDouble)).map(_.toArray)
                                                 .map(line => Vectors.dense(line)))

    // Compute the top 4 singular values and corresponding singular vectors.
    val svd = mat.computeSVD(4, computeU = true)
    val U: RowMatrix = svd.U  // The U factor is a RowMatrix.
    val s: Vector = svd.s  // The singular values are stored in a local dense vector.
    val V: Matrix = svd.V  // The V factor is a local dense matrix.
    
    println(s);
    println("-------------------");
    println(V);
    sc.stop();
}
}