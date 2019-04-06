#!/bin/bash

BASE_DIR="$(dirname $0)"
COMMON_DIR=$BASE_DIR

source $COMMON_DIR/env.sh

kubectl apply -f $BASE_DIR/../../k8s/zeppelin/zeppelin-server.yaml