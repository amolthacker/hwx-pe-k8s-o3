#!/bin/bash

BASE_DIR="$(dirname $0)"
COMMON_DIR=$BASE_DIR

source $COMMON_DIR/env.sh

cd ${ISTIO_DIR}
export PATH=$PWD/bin:$PATH
echo "Init istio CRDs ..."
helm install install/kubernetes/helm/istio-init --name istio-init --namespace istio-system
sleep 120
kubectl get crds | grep 'istio.io\|certmanager.k8s.io' | wc -l

echo "Install istio ..."
helm install install/kubernetes/helm/istio --name istio --namespace istio-system
sleep 180

echo "Enable istio injection ..."
kubectl label namespace default istio-injection=enabled
kubectl get namespace -L istio-injection