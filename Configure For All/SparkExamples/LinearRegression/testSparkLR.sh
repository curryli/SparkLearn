/opt/cloudera/parcels/CDH-5.6.0-1.cdh5.6.0.p0.45/lib/spark/bin/spark-submit \
--class LinearRegression \
--master yarn \
--executor-memory 4G \
--total-executor-cores 16 \
LinearRegression.jar \
