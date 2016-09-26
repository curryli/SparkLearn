/opt/cloudera/parcels/CDH-5.6.0-1.cdh5.6.0.p0.45/lib/spark/bin/spark-submit \
--class myPCA2 \
--master yarn \
--deploy-mode cluster \
--queue root.default \
--executor-memory 2G \
--total-executor-cores 20 \
pca.jar \

