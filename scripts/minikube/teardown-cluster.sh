#!/bin/bash

echo
echo "------------------------------------------------"
echo "Tearing down any existing Minikube instances ..."
echo "------------------------------------------------"
minikube stop
minikube delete
echo "------------------------------------------------"
echo