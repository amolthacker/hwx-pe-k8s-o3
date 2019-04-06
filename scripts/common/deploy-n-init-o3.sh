#!/bin/bash

BASE_DIR="$(dirname $0)"
COMMON_DIR=$BASE_DIR

source $COMMON_DIR/env.sh

cd $OZ_DIST_DIR
skaffold run
cd $PROJ_DIR
kubectl get pods,statefulset,svc -n default
echo "Waiting for pods to come up ..."
sleep 300
echo
echo "-----------------------------------------------"
echo
kubectl get pods,statefulset,svc -n default
echo
kubectl exec om-0 ozone version

s3g_svc_ip=$1
s3g_svc_port=$(kubectl describe svc s3g-public | grep "NodePort:" | awk '{print $3}' | awk 'BEGIN {FS = "/"} {print $1}')
s3g_endpoint="http://$s3g_svc_ip:$s3g_svc_port"
echo "-----------------------------------------------"
echo "Ozone S3 Gateway - $s3g_endpoint"
echo "-----------------------------------------------"
echo

echo "-----------------------------------------------"
echo "Initializing Ozone S3 Bucket ..."
echo "-----------------------------------------------"
aws s3api --endpoint $s3g_endpoint create-bucket --bucket valuations
aws s3api --endpoint $s3g_endpoint list-buckets
