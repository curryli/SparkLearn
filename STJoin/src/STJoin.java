import java.io.IOException;
import java.util.*;
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



public class STJoin {
	public static int time = 0;

	 

    /*

     * map将输出分割child和parent，然后正序输出一次作为右表，

     * 反序输出一次作为左表，需要注意的是在输出的value中必须

     * 加上左右表的区别标识。

     */

    public static class Map extends Mapper<Object, Text, Text, Text> {

 

        // 实现map函数

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            String childname = new String();// 孩子名称
            String parentname = new String();// 父母名称
            String relationtype = new String();// 左右表标识
            
            // 输入的一行预处理文本

            String line = value.toString();
            String[] values = line.split("\\s+");
            
            
            if (values[0].compareTo("child") != 0) {
                childname = values[0];
                parentname = values[1];

                // 输出左表
                relationtype = "1";
                context.write(new Text(values[1]), new Text(relationtype +
                        "+"+ childname + "+" + parentname));


                // 输出右表
                relationtype = "2";
                context.write(new Text(values[0]), new Text(relationtype +
                        "+"+ childname + "+" + parentname));
            }
        }
    }


    public static class Reduce extends Reducer<Text, Text, Text, Text> {
        // 实现reduce函数

        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            // 输出表头
            if (0 == time) {
                context.write(new Text("grandchild"), new Text("grandparent"));
                time++;
            }
 
            int grandchildnum = 0;
            String[] grandchild = new String[10];
            int grandparentnum = 0;
            String[] grandparent = new String[10];

            Iterator<Text> ite = values.iterator();  //values是关于一到多条record的list

            while (ite.hasNext()) {
                String record = ite.next().toString();  //每一个record 都是类似  "1+Jone+Jack"这样一条记录
                int len = record.length();
                int i = 2;
                if (0 == len) 
                	continue;
              
                // 取得左右表标识
                char relationtype = record.charAt(0);
                // 定义孩子和父母变量
                String childname = new String();
                String parentname = new String();


                // 获取value-list中value的child
                while (record.charAt(i) != '+') {
                    childname += record.charAt(i);
                    i++;
                }
                
                i = i + 1; //再跳过一个'+'

                // 获取value-list中value的parent
                while (i < len) {
                    parentname += record.charAt(i);
                    i++;
                }


                // 左表，取出child放入grandchildren
                if ('1' == relationtype) {
                    grandchild[grandchildnum] = childname;
                    grandchildnum++;
                }

                // 右表，取出parent放入grandparent
                if ('2' == relationtype) {
                    grandparent[grandparentnum] = parentname;
                    grandparentnum++;
                }
            }

 

            // grandchild和grandparent数组求笛卡尔儿积

            if (0 != grandchildnum && 0 != grandparentnum) {
                for (int m = 0; m < grandchildnum; m++) {
                    for (int n = 0; n < grandparentnum; n++) {
                        // 输出结果
                        context.write(new Text(grandchild[m]), new Text(grandparent[n]));
                    }
                }
            }
        }
    }
	
    public static void main(String[] args) throws Exception {
  	  Configuration conf = new Configuration();  //从配置文件读取参数
  	  
  	String[] ioArgs=new String[]{"-Dmapreduce.job.queuename=root.default","xrli/STJoin_in","xrli/STJoin_out"};
    String[] otherArgs = new GenericOptionsParser(conf, ioArgs).getRemainingArgs();

  	//String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
  	
  	  //GenericOptionsParser这个类，它的作用是将命令行中参数自动设置到变量conf中。很轻松就可以将参数与代码分离开。
  	  if (otherArgs.length != 2) {
  	    System.err.println("Usage: Single Table Join <in> <out>");
  	    System.exit(2);
  	  }
  	  
  	Job job = new Job(conf, "Single Table Join");  	 
  	job.setJarByClass(STJoin.class);

    // 设置Map和Reduce处理类
    job.setMapperClass(Map.class);
    job.setReducerClass(Reduce.class);

    // 设置输出类型
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    // 设置输入和输出目录
    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
 	}


}

