#!/bin/bash

BASE_DIR="$(dirname $0)"
COMMON_DIR=$BASE_DIR
source $COMMON_DIR/env.sh

helm install stable/prometheus-operator --name prometheus-operator --namespace monitoring
