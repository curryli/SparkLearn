

object Hello{
    def main(args :Array[String]){ 
     
      val a = List((1,"a"),(2,"b"))
      a.filter(temp => temp._1>1).foreach(println)
    }
}

//temp = (f,s)=> f==1).foreach(println