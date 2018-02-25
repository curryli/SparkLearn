 
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import scala.util.control.Breaks._
/**
 * Created by xuyao on 15-7-24.
 * 求中位数，数据是分布式存储的
 * 将整体的数据分为K个桶，统计每个桶内的数据量，然后统计整个数据量
 * 根据桶的数量和总的数据量，可以判断数据落在哪个桶里，以及中位数的偏移量
 * 取出这个中位数
 * 
 * 数据是1 2 3 4 5 6 8 9 11 12 13 15 18 20 22 23 25 27 29
 */
object Median {
   def main (args: Array[String]) {
     // 屏蔽不必要的日志显示在终端上
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
    
    val conf =new SparkConf().setAppName("Median")
     val sc=new SparkContext(conf)
     //通过textFile读入的是字符串型，所以要进行类型转换
     val data =sc.textFile("xrli/median_data.txt").flatMap(x=>x.split(' ')).map(x=>x.toInt)
     //将数据分为4组，当然我这里的数据少
     val  mappeddata =data.map(x=>(x/4,x)).sortByKey()
     //p_count为每个分组的个数
     val p_count =data.map(x=>(x/4,1)).reduceByKey(_+_).sortByKey()
     p_count.foreach(println)
     //p_count是一个RDD，不能进行Map集合操作，所以要通过collectAsMap方法将其转换成scala的集合
     val scala_p_count=p_count.collectAsMap() //　功能和collect函数类似。该函数用于Pair RDD
     //根据key值得到value值
     println(scala_p_count(0))
     //sum_count是统计总的个数，不能用count(),因为会得到多少个map对。
     val sum_count = p_count.map(x=>x._2).sum.toInt
     println(sum_count)
     
     var temp = 0
     var index = 0
     var mid = sum_count/2
     var flag = false
     var i=0
     while(i<4 && !flag) {
       temp=temp+scala_p_count(i)
       i+=1
       if(temp >= mid) {
       index=i
       flag = true
       }     
     }
     /*中位数在桶中的偏移量*/
     val offset = temp - mid
     //println("offset is " + offset)
     /*获取到中位数所在桶中的偏移量为offset的数，也就是中位数*/
     val result = mappeddata.filter(num => num._1 == index).takeOrdered(offset+1)
     //takeOrdered它默认可以将key从小到大排序后，获取rdd中的前n个元素
     println("Median is " + result(offset)._2)
     sc.stop()
  }

}