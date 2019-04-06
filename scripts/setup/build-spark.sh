#!/bin/bash

export spark-version=3.0.0
export hadoop-version=3.2
export spark_dist_dir=<SPARK_DIST_DIR>

git clone https://github.com/amolthacker/spark.git
cd spark
git checkout 3.0.0-SNAPSHOT-a15f17ce277
#./dev/change-scala-version.sh 2.11
# update parent pom as required
./dev/make-distribution.sh --name hadoop${hadoop_version} --pip --r --tgz -Psparkr -Phadoop-${hadoop_version} -Pyarn -Pkubernetes

export spark_dist=spark-${spark_version}-SNAPSHOT-bin-hadoop${hadoop_version}
cp ${spark_dist}.tgz ${spark_dist_dir}/.
cd ${spark_dist_dir}
tar -xvzf ${spark_dist}.tgz
cd ${spark_dist}

eval $(minikube docker-env)
./bin/docker-image-tool.sh -m -r amolthacker -t ${spark_version} build
./bin/docker-image-tool.sh -m -r amolthacker -t ${spark_version} push