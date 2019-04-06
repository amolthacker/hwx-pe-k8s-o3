#!/bin/bash

# Configuring tiller ...
kubectl --namespace kube-system create sa tiller
kubectl create clusterrolebinding tiller \
    --clusterrole cluster-admin \
    --serviceaccount=kube-system:tiller

echo "Initializing helm ..."
helm init --service-account tiller
helm repo update

echo "Verifying helm ..."
kubectl get deploy,svc tiller-deploy -n kube-system
sleep 120