import org.apache.spark.SparkConf
import org.apache.spark.mllib.clustering.StreamingKMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.log4j.{Level, Logger}
import breeze.linalg.DenseVector
import org.apache.spark.mllib.regression.{StreamingLinearRegressionWithSGD, LabeledPoint}
import org.apache.spark.storage.StorageLevel

object StreamingKMeansExample {

  def main(args: Array[String]) {
    
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val conf = new SparkConf().setAppName("StreamingKMeansExample")
    val ssc = new StreamingContext(conf, Seconds(5))

    val stream = ssc.socketTextStream(args(0), args(1).toInt, StorageLevel.MEMORY_AND_DISK_SER)
    
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
 
    
    val model = new StreamingKMeans()
      .setK(3)
      .setDecayFactor(1.0)
      .setRandomCenters(4, 0.0)

    model.trainOn(FeatureStream)

    //model.predictOn(FeatureStream).print()
    model.predictOnValues(labeledStream.map(lp => (lp.label, lp.features))).print()

    
    ssc.start()
    ssc.awaitTermination()
    
  }
}
 