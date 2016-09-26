import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

 

public class Dedup {
    //map将输入中的value复制到输出数据的key上，并直接输出
    public static class Map extends Mapper<Object,Text,Text,Text>{
        private static Text line = new Text();//每行数据
        public void map(Object key,Text value,Context context)
                throws IOException,InterruptedException{
            line = value;
            context.write( line , new Text(""));
        }
    }
        
    //reduce将输入中的key复制到输出数据的key上，并直接输出
    public static class Reduce extends Reducer<Text,Text,Text,Text>{
        //实现reduce函数
        public void reduce(Text key,Iterable<Text> values,Context context)
                throws IOException,InterruptedException{
            context.write(key, new Text(""));
        }
    }
        
        
    public static void main(String[] args) throws Exception {
  	  Configuration conf = new Configuration();
  	  String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
  	  //GenericOptionsParser这个类，它的作用是将命令行中参数自动设置到变量conf中。很轻松就可以将参数与代码分离开。
  	  if (otherArgs.length != 2) {
  	    System.err.println("Usage: WordCount <in> <out>");
  	    System.exit(2);
  	  }
  	Job job = new Job(conf, "Data Deduplication");
  	 
  	job.setJarByClass(Dedup. class );
  	job.setMapperClass(Map. class );
    job.setCombinerClass(Reduce. class );
    job.setReducerClass(Reduce. class );
    job.setOutputKeyClass(Text. class );
    job.setOutputValueClass(Text. class );
  	  FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
  	  FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
  	  System.exit(job.waitForCompletion(true) ? 0 : 1);
  	}


}

