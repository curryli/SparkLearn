/opt/cloudera/parcels/CDH-5.6.0-1.cdh5.6.0.p0.45/lib/spark/bin/spark-submit \
--class WindowWordCount \
--master local[8] \
SparkStream.jar \
localhost 9999 30 10
