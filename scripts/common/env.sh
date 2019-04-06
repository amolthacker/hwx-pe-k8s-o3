#!/bin/bash

#ISTIO_DIR=<ISTIO_DIR>

WKSPC_BASE=<BASE_DIR>
PROJ_DIR="$WKSPC_BASE/amolthacker/hwx-pe-k8s-o3"

OZ_BASE_DIR="$WKSPC_BASE/amolthacker/hadoop/hadoop-ozone"
OZ_DIST_DIR="$OZ_BASE_DIR/dist"

APP_IMG_NAME='amolthacker/ce-o3:latest'

export AWS_ACCESS_KEY_ID=oz
export AWS_SECRET_ACCESS_KEY=ozs
export AWS_DEFAULT_REGION=us-east-1
export AWS_DEFAULT_OUTPUT=json

export MKUBE_CPUS=6
export MKUBE_MEM_MB=8192
export MKUBE_DISK=30g

export GCLOUD_REGION=us-east4
export GCLOUD_ZONE=us-east4-a
