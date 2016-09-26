import org.apache.spark.SparkConf
import org.apache.spark.mllib.clustering.StreamingKMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.log4j.{Level, Logger}
import breeze.linalg.DenseVector
import org.apache.spark.mllib.regression.{StreamingLinearRegressionWithSGD, LabeledPoint}
import org.apache.spark.mllib.classification.StreamingLogisticRegressionWithSGD
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.storage.StorageLevel

 
object testFileAsSrc {
  def main(args: Array[String]) {
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
    
    val conf = new SparkConf().setAppName("SocketWordCount")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(1))
 
    // 通过Socket获取数据，该处需要提供Socket的主机名和端口号，数据保存在内存和硬盘中
    val stream = ssc.socketTextStream(args(0), args(1).toInt, StorageLevel.MEMORY_AND_DISK_SER)
 
    val NumFeatures = 4
    val zeroVector = DenseVector.zeros[Double](NumFeatures)
    val model = new StreamingLogisticRegressionWithSGD()
      .setInitialWeights(Vectors.dense(zeroVector.data))
      .setNumIterations(1)
      .setStepSize(0.01)

    // create a stream of labeled points
    val labeledStream = stream.map{ line =>
      val fields = line.split(",")
      val y = fields(4).toInt
      val features = Vectors.dense(fields(0).toDouble, fields(1).toDouble, fields(2).toDouble, fields(3).toDouble)
      LabeledPoint(label = y, features = features)
    }

    val FeatureStream = stream.map{ line =>
      val fields = line.split(",")
      Vectors.dense(fields(0).toDouble, fields(1).toDouble, fields(2).toDouble, fields(3).toDouble)
    }
    
    // train and test model on the stream, and print predictions for illustrative purposes
    model.trainOn(labeledStream)
    
    //model.predictOn(FeatureStream).print()
   
    model.predictOnValues(labeledStream.map(lp => (lp.label, lp.features))).print()
    
    ssc.start()
    ssc.awaitTermination()
  }
}