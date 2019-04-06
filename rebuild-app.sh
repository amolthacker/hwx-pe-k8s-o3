#!/bin/bash

BASE_DIR="$(dirname $0)"
SCRIPTS_DIR="${BASE_DIR}/scripts"

source $SCRIPTS_DIR/common/env.sh

echo
echo "-----------------------------------------------"
echo "Rebuilding App ..."
echo "-----------------------------------------------"
mvn clean package -DskipDocker
echo "-----------------------------------------------"
echo

echo "-----------------------------------------------"
echo "Rebuilding Docker image ..."
echo "-----------------------------------------------"
docker login
docker build -t $IMG_NAME .
docker push $IMG_NAME
echo "-----------------------------------------------"
echo
