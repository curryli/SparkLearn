import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
 
object PageRank {
  def main(args: Array[String]) {
    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
 
    //设置运行环境
    val conf = new SparkConf().setAppName("PageRank").setMaster("local")
    val sc = new SparkContext(conf)
 
    //读入数据文件
    val articles: RDD[String] = sc.textFile("xrli/graphx-wiki-vertices.txt")
    val links: RDD[String] = sc.textFile("xrli/graphx-wiki-edges.txt")
 
    //装载顶点和边
    val vertices = articles.map { line =>
      val fields = line.split('\t')
      (fields(0).toLong, fields(1))
    }
 
    val edges = links.map { line =>
      val fields = line.split('\t')
      Edge(fields(0).toLong, fields(1).toLong, 0)
    }
 
    //cache操作
    //val graph = Graph(vertices, edges, "").persist(StorageLevel.MEMORY_ONLY_SER)
    val graph = Graph(vertices, edges, "").persist()    
    //val graph = Graph(vertices, edges).persist()  //第三个是默认顶点属性，可以为空，所以这样也行
    //graph.unpersistVertices(false)
 
    //测试
    println("**********************************************************")
    println("获取5个triplet信息")
    println("**********************************************************")
    graph.triplets.take(5).foreach(println(_))
 
    //pageRank算法里面的时候使用了cache()，故前面persist的时候只能使用MEMORY_ONLY
    println("**********************************************************")
    println("PageRank计算，获取最有价值的数据")
    println("**********************************************************")
    val prGraph = graph.pageRank(0.001).cache()  //传入的这个参数的值越小 就越精确，但越耗时
 
    val titleAndPrGraph = graph.outerJoinVertices(prGraph.vertices) {
      (v, title, rank) => (rank.getOrElse(0.0), title)
    }
 
    //graph.outerJoinVertices(prGraph.vertices)： 对原图graph外联prGraph.vertices形成一个新图。
   //然后用(v, title, rank) => (rank.getOrElse(0.0), title) outerJoinVertices 这句话对新图中的每个顶点进行map操作。
    
    titleAndPrGraph.vertices.top(10) {
      Ordering.by((entry: (VertexId, (Double, String))) => entry._2._1)
    }.foreach(t => println(t._2._2 + ": " + t._2._1))
 
    sc.stop()
  }
}