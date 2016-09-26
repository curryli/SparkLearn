/opt/cloudera/parcels/CDH-5.6.0-1.cdh5.6.0.p0.45/lib/spark/bin/spark-submit \
--class WordCount \
--master yarn \
--executor-memory 2G \
--total-executor-cores 20 \
SparkScala.jar \
xrli/spark_in/testWordCount.txt \
xrli/spark_out

