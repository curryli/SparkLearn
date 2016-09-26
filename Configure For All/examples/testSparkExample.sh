/opt/cloudera/parcels/CDH-5.6.0-1.cdh5.6.0.p0.45/lib/spark/bin/spark-submit \
--class org.apache.spark.examples.SparkPi \
--master yarn \
--executor-memory 2G \
--total-executor-cores 20 \
/opt/cloudera/parcels/CDH-5.6.0-1.cdh5.6.0.p0.45/lib/spark/examples/lib/spark-examples-1.5.0-cdh5.6.0-hadoop2.6.0-cdh5.6.0.jar \
100
