/opt/cloudera/parcels/CDH-5.6.0-1.cdh5.6.0.p0.45/lib/spark/bin/spark-submit \
--class StreamingSimulation \
--master local[2] \
SparkStream.jar \
people.txt 9999 1000