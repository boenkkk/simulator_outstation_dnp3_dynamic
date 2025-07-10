#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Define variables
APP_NAME="simulator-outstation-dnp3-dynamic"
DOCKERFILE="DOCKERFILE"
REGISTRY_PATH="1.2.3.4/scada"
NAMESPACE="simulator-dnp3"
DEPLOY_FILE="k8s-deploy.yaml"
FULL_IMAGE_NAME="$REGISTRY_PATH/$APP_NAME"

### DOCKER BUILD
# Login to Docker
echo "Logging into Docker..."
docker login || { echo "Docker login failed!"; exit 1; }

# Build the Docker image
echo "Building Docker image..."
docker build -t $APP_NAME:latest -f $DOCKERFILE . || { echo "Docker build failed!"; exit 1; }

# List images and filter by name
echo "Checking if the image was built successfully..."
docker images | grep $APP_NAME || { echo "Image not found!"; exit 1; }

# Tag the image for the remote repository
if [ -n "$REGISTRY_PORT" ]; then
    FULL_IMAGE_NAME="$REGISTRY_PATH/$APP_NAME"
fi

echo "Tagging image as $FULL_IMAGE_NAME..."
docker tag $APP_NAME:latest $FULL_IMAGE_NAME || { echo "Docker tag failed!"; exit 1; }

# Push the image to the remote repository
echo "Pushing image to registry..."
docker push $FULL_IMAGE_NAME || { echo "Docker push failed!"; exit 1; }

### KUBERNETES DEPLOYMENT
# Delete the existing deployment
echo "Deleting existing deployment..."
kubectl delete -f $DEPLOY_FILE --ignore-not-found

# Apply the Kubernetes deployment
echo "Applying Kubernetes deployment..."
kubectl apply -f $DEPLOY_FILE || { echo "Kubernetes deployment failed!"; exit 1; }

# Display deployed resources
echo "Deployed Resources status:"
kubectl get all -n "$NAMESPACE" | grep $APP_NAME

echo "Deployment completed successfully!"
