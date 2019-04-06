#!/bin/bash

BASE_DIR="$(dirname $0)"
COMMON_DIR=$BASE_DIR

source $COMMON_DIR/env.sh

#o3_s3g_svc_ip=$(minikube ip)
o3_s3g_svc_ip=$1
echo "o3_s3g_svc_ip: $o3_s3g_svc_ip"

echo
echo "-----------------------------------------------"
echo "Setting up Helm ..."
echo "-----------------------------------------------"
sh $COMMON_DIR/setup-helm.sh
echo "-----------------------------------------------"
echo

echo "-----------------------------------------------"
echo "Deplpying Istio ..."
echo "-----------------------------------------------"
sh $COMMON_DIR/setup-istio.sh
echo "-----------------------------------------------"
echo

echo "-----------------------------------------------"
echo "Deplpying prometheus operator ..."
echo "-----------------------------------------------"
sh $COMMON_DIR/deploy-prom.sh
echo "-----------------------------------------------"
echo

echo "-----------------------------------------------"
echo "Deploying Apache Hadoop Ozone on K8s ..."
echo "-----------------------------------------------"
sh $COMMON_DIR/deploy-n-init-o3.sh $o3_s3g_svc_ip
echo "-----------------------------------------------"
echo

echo "-----------------------------------------------"
echo "Deploying Compute Cluster ..."
echo "-----------------------------------------------"
sh $COMMON_DIR/deploy-hwxpe.sh $o3_s3g_svc_ip
echo "-----------------------------------------------"
echo

sleep 60

echo "-----------------------------------------------"
echo "Deploying Zeppelin ..."
echo "-----------------------------------------------"
sh $COMMON_DIR/deploy-zeppelin.sh
echo "-----------------------------------------------"
echo