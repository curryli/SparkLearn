import org.apache.spark.graphx._  
import org.apache.spark.SparkContext  
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.rdd.RDD
// Import random graph generation library  
//import org.apache.spark.graphx.util.GraphGenerators  
import org.apache.spark.graphx.lib.ShortestPaths
  
/**  
 * Created by jack on 3/4/14.  
 */  
object Floyd {  
    def main(args: Array[String]) {  
      
       //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
 
    
        //设置运行环境
        val conf = new SparkConf().setAppName("SimpleGraphX") 
        val sc = new SparkContext(conf) 
        
        
      //设置顶点和边，注意顶点和边都是用元组定义的Array
    //顶点的数据类型是VD:(String,Int)
      val vertexArray = Array(
      (1L, ("Alice", 28)),
      (2L, ("Bob", 27)),
      (3L, ("Charlie", 65)),
      (4L, ("David", 42)),
      (5L, ("Ed", 55)),
      (6L, ("Fran", 50))
    )
    //边的数据类型ED:Int
    val edgeArray = Array(
      Edge(2L, 1L, 7),
      Edge(2L, 4L, 2),
      Edge(3L, 2L, 4),
      Edge(3L, 6L, 3),
      Edge(4L, 1L, 1),
      Edge(5L, 2L, 2),
      Edge(5L, 3L, 8),
      Edge(5L, 6L, 3)
    )
 
    //构造vertexRDD和edgeRDD
    val vertexRDD: RDD[(Long, (String, Int))] = sc.parallelize(vertexArray)
    val edgeRDD: RDD[Edge[Int]] = sc.parallelize(edgeArray)
 
    //构造图Graph[VD,ED]
    val graph: Graph[(String, Int), Int] = Graph(vertexRDD, edgeRDD)
 
 
    
    val sourceId: VertexId = 5L // 定义源点
    
    //初始化各节点到原点的距离  
    val initialGraph = graph.mapVertices((id, _) => if (id == sourceId) 0.0 else Double.PositiveInfinity)

    println(initialGraph.vertices.collect.mkString("\n")) 
    println(initialGraph.edges.collect.mkString("\n")) 
    
    val sssp = initialGraph.pregel(Double.PositiveInfinity)(
      // Vertex Program，节点处理消息的函数，dist为原节点属性（Double），newDist为消息类型（Double）  
      (id, dist, newDist) => math.min(dist, newDist),
      sendMessage,
      (a,b) => math.min(a,b) // 最短距离
    )
    println(sssp.vertices.collect.mkString("\n"))
 
    sc.stop()
  }
    

    def sendMessage(triplet:  EdgeTriplet[Double, Int]): Iterator[(VertexId, Double)] = {
      if (triplet.srcAttr + triplet.attr < triplet.dstAttr) {
          Iterator((triplet.dstId, triplet.srcAttr + triplet.attr))
        } else {
          Iterator.empty
        }
    }
    
 
    
    
}