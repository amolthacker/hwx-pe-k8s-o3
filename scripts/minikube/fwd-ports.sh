#!/bin/bash

# Ozone S3 Gateway
kubectl port-forward s3g-0 9878:9878 &

# Ozone Manager
kubectl port-forward om-0 9874:9874 &

# Prometheus
kubectl port-forward -n monitoring prometheus-prometheus-operator-prometheus-0 9090 &
# URL: http://localhost:9090

# Prometheus - Alerts Manager
kubectl port-forward -n monitoring alertmanager-prometheus-operator-alertmanager-0 9093 &
# URL: http://localhost:9093/#/alerts

# Prometheus - Grafana
kubectl port-forward $(kubectl get pods --selector=app=grafana -n monitoring --output=jsonpath="{.items..metadata.name}") -n monitoring 3000 &
# URL: http://localhost:3000
# Default credentials
# User: admin
# Pass: prom-operator


# Zeppelin
kubectl port-forward zeppelin-server 8080:80


