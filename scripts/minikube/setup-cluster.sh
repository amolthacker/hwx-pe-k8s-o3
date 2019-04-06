#!/bin/bash

BASE_DIR="$(dirname $0)"
COMMON_DIR="$BASE_DIR/../common"

source $COMMON_DIR/env.sh

echo "--------------------------------------------------"
echo "Spinning up Minikube ..."
echo "--------------------------------------------------"
minikube start --cpus=$MKUBE_CPUS --memory=$MKUBE_MEM_MB --disk-size=$MKUBE_DISK
echo "--------------------------------------------------"
echo