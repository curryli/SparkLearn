/opt/cloudera/parcels/CDH-5.6.0-1.cdh5.6.0.p0.45/lib/spark/bin/spark-submit --jar SparkScala.jar --class WordCount --args yarn-standalone --args hdfs://xrli/spark_in --args hdfs://xrli/spark_out --num-workers 1 --master-memory 512m --worker-memory 1g --worker-cores 1 
