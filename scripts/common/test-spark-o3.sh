#!/bin/bash

bin/spark-submit \
    --master k8s://https://192.168.99.100:8443 \
    --deploy-mode cluster \
    --name spark-word-count \
    --class org.apache.spark.examples.JavaWordCount \
    --conf spark.executor.instances=1 \
    --conf spark.kubernetes.namespace=default \
    --conf spark.kubernetes.authenticate.driver.serviceAccountName=spark \
    --conf spark.kubernetes.container.image=amolthacker/spark-ozone:3.0.0 \
    --conf spark.kubernetes.container.image.pullPolicy=Always \
    --jars /opt/hadoop-ozone-filesystem-lib-legacy.jar \
    local:///opt/spark/examples/jars/spark-examples_2.11-2.4.0.jar \
    o3fs://test.s3oz/test.txt