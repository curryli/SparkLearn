/opt/cloudera/parcels/CDH-5.6.0-1.cdh5.6.0.p0.45/lib/spark/bin/spark-submit \
--class testFileAsSrc \
--master local[2] \
StreamingKmeans.jar \
localhost 9999
