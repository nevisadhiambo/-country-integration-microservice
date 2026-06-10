#!/bin/bash
set -e

echo "=== Building Docker image ==="
mvn clean package -DskipTests
docker build -t your-dockerhub-username/country-integration:latest .
docker push your-dockerhub-username/country-integration:latest

echo "=== Applying Kubernetes manifests ==="
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/mysql.yaml
kubectl wait --for=condition=ready pod -l app=mysql -n country-app --timeout=120s
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/hpa.yaml

echo "=== Deployment complete ==="
kubectl get pods -n country-app
kubectl get services -n country-app