#!/bin/bash

BASE_DIR="$(dirname $0)"
COMMON_DIR=$BASE_DIR

source $COMMON_DIR/env.sh

OZ_BASE_VERSION="0.4.0-SNAPSHOT"
OZ_LOCAL_VERSION="$OZ_BASE_VERSION-8d1c2184283"
OZ_GROUP_ID="org.apache.hadoop"
OZ_COMPONENT_PFX="hadoop-ozone"

OZ_FS_LIB_DIR_PFX="$OZ_BASE_DIR/ozonefs-lib"
OZ_FS_LIB_COMPONENT="filesystem-lib"

function mvn_install_file {
    artifact=$1
    artifact_file=$2
    echo
    echo "----------------------------------------------------------"
    echo "mvn install $artifact"
    echo "----------------------------------------------------------"

    mvn install:install-file \
        -Dfile=$artifact_file \
        -DgroupId=$OZ_GROUP_ID \
        -DartifactId=$artifact \
        -Dversion=$OZ_LOCAL_VERSION \
        -Dpackaging=jar \
        -DgeneratePom=true
    echo "----------------------------------------------------------"
}

declare -a OZ_COMPONENTS=("common" "ozone-manager" "objectstore-service" "s3gateway" "client")
for comp in ${OZ_COMPONENTS[@]}; do
    artifact="$OZ_COMPONENT_PFX-$comp"
    artifact_file="$OZ_BASE_DIR/$comp/target/$artifact-$OZ_BASE_VERSION.jar"
    mvn_install_file $artifact $artifact_file
done

declare -a OZ_FS_LIB_COMPONENTS=("current" "legacy")
for typ in ${OZ_FS_LIB_COMPONENTS[@]};do
    artifact="$OZ_COMPONENT_PFX-$OZ_FS_LIB_COMPONENT-$typ"
    artifact_file="$OZ_FS_LIB_DIR_PFX-$typ/target/$artifact-$OZ_BASE_VERSION.jar"
    mvn_install_file $artifact $artifact_file
done


echo "----------------------------------------------------------"
echo
