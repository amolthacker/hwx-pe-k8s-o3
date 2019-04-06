#!/bin/bash

BASE_DIR="$(dirname $0)"
SCRIPTS_DIR="${BASE_DIR}/scripts"

source $SCRIPTS_DIR/common/env.sh

#sh $BASE_DIR/rebuild-app.sh

sh $SCRIPTS_DIR/minikube/teardown-cluster.sh

sh $SCRIPTS_DIR/minikube/setup-cluster.sh

o3_s3g_svc_ip=$(minikube ip)
echo "o3_s3g_svc_ip: $o3_s3g_svc_ip"
sh $SCRIPTS_DIR/common/deploy-app.sh $o3_s3g_svc_ip

