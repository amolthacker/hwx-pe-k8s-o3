#!/bin/bash

BASE_DIR="$(dirname $0)"

export spark_dist_dir=<SPARK_DIST_DIR> # dir where the build spark-dist (eg: spark-3.0.0-SNAPSHOT-bin-hadoop3.2.tgz) was unzipped (eg: spark-3.0.0-SNAPSHOT-bin-hadoop3.2)
export ozone_dist_dir=$HADOOP_DIR/hadoop-ozone/dist

# spin up ozone cluster
cd ${ozone_dist_dir}
minikube start
skaffold run

# once the cluster is up - copy the necessary files
cd ${spark_dist_dir}
mkdir docker
cd docker
cp ${BASE_DIR}/../spark-ozone/Dockerfile .
cp ${BASE_DIR}/../spark-ozone/core-site.xml .
kubectl cp om-0:/opt/hadoop/etc/hadoop/ozone-site.xml .
kubectl cp om-0:/opt/hadoop/share/ozone/lib/hadoop-ozone-filesystem-lib-legacy-0.4.0-SNAPSHOT.jar .

# build the image
docker build -t amolthacker/spark-ozone:3.0.0 .
docker push amolthacker/spark-ozone:3.0.0


