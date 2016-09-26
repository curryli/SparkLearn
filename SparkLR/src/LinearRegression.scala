import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
 
object LinearRegression {
  def main(args:Array[String]): Unit ={
    // 屏蔽不必要的日志显示终端上
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
 
    // 设置运行环境
    val conf = new SparkConf().setAppName("Linear Regression")
    val sc = new SparkContext(conf)
 
    
    // Load and parse the data
    val data = sc.textFile("xrli/lpsa.data")
     val parsedData = data.map { line =>
      val y = line.split(',')(0).toDouble
      val x = line.split(',')(1).split(' ').map(_.toDouble)
      LabeledPoint(y, Vectors.dense(x))
    }.cache
 
    // Building the model
    val numIterations = 100
    val model = LinearRegressionWithSGD.train(parsedData, numIterations)
 
    // Evaluate model on training examples and compute training error
    val valuesAndPreds = parsedData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
 
    val MSE = valuesAndPreds.map{ case(v, p) => math.pow((v - p), 2)}.reduce (_ + _) / valuesAndPreds.count
    println("training Mean Squared Error = " + MSE)
 
    sc.stop()
  }
}