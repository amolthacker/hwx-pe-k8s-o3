#!/bin/bash

git clone https://github.com/amolthacker/zeppelin.git
cd zeppelin
git checkout 0.9.0-SNAPSHOT-97c845a6f39
mvn clean package -DskipTests -Pbuild-distr
cp zeppelin-distribution/target/zeppelin-0.9.0-SNAPSHOT.tar.gz scripts/docker/zeppelin/bin/.
cd scripts/docker/zeppelin/bin

eval $(minikube docker-env)
docker build -t amolthacker/zeppelin:0.9.0-snapshot .
docker push amolthacker/zeppelin:0.9.0-snapshot