import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.classification.LogisticRegressionWithSGD
import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkContext, SparkConf}
 
object LinearRegression {
  def main(args:Array[String]): Unit ={
    // 屏蔽不必要的日志显示终端上
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
 
    // 设置运行环境
    val conf = new SparkConf().setAppName("Linear Regression")
    val sc = new SparkContext(conf)
 
    val rawData=sc.textFile("xrli/train_noheader.tsv")
    val records=rawData.map(_.split("\t"))

    val category=records.map(r=>r(3)).distinct().collect().zipWithIndex.toMap

    val data=records.map{point=>
      val replaceData=point.map(_.replaceAll("\"",""))
      val label=replaceData(replaceData.size-1).toInt
      val categoriesIndex=category(point(3))
      val categoryFeatures=Array.ofDim[Double](category.size)
      categoryFeatures(categoriesIndex)=1.0
      val otherfeatures=replaceData.slice(4,replaceData.size-1).map(x=>if(x=="?") 0.0 else x.toDouble)
      val features=otherfeatures++categoryFeatures
      LabeledPoint(label,Vectors.dense(features))
    }

    val vectors=data.map(p=>p.features)
    val scaler=new StandardScaler(withMean = true,withStd = true).fit(vectors)
    val scalerData=data.map(point=>
      LabeledPoint(point.label,scaler.transform(point.features))
    )

    val lrModel=LogisticRegressionWithSGD.train(scalerData,10)

    val predictrueData=scalerData.map{point=>
      if(lrModel.predict(point.features)==point.label) 1 else 0
    }.sum()

    val accuracy=predictrueData/data.count()
    println(accuracy)
	sc.stop()
  }
}