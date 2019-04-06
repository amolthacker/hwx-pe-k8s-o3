#!/bin/bash

BASE_DIR="$(dirname $0)"
COMMON_DIR=$BASE_DIR

source $COMMON_DIR/env.sh

#svc_ip=$(minikube ip)
svc_ip=$1

helm install -n hwxpe helm/hwxpe
sleep 60
echo
kubectl expose service ve-ctrl --type=NodePort --name=ve-ctrl-public
vectrl_svc_port=$(kubectl describe svc ve-ctrl-public | grep "NodePort:" | grep port-2 | awk '{print $3}' | awk 'BEGIN {FS = "/"} {print $1}')
echo
echo "-----------------------------------------------"
sleep 180
echo
helm status hwxpe
echo
echo "------------------------------------------------------------"
echo "Compute Engine Service: http://$svc_ip:$vectrl_svc_port"
echo "------------------------------------------------------------"
echo
