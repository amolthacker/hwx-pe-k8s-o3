#!/bin/bash

brew install protobuf@2.5
echo 'export PATH="/usr/local/opt/protobuf@2.5/bin:$PATH"' >> ~/.bash_profile
source ~/.bash_profile
protoc --version
git clone https://github.com/amolthacker/hadoop.git
cd hadoop/
git checkout ozone-0.4-8d1c2184283
mvn clean package -DskipTests=true -Dmaven.javadoc.skip=true -Phdds -Pdist -Dtar -DskipShade

# only hadoop-ozone
# mvn clean package install -Phdds -DskipTests=true -Dmaven.javadoc.skip=true -Pdist -Dtar -DskipShade -am -pl :hadoop-ozone-dist
