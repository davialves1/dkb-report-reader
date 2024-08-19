#!/bin/bash

GREEN='\033[0;32m'
NC='\033[0m'

echo "Building application"
mvn clean package

echo "Building new image"
docker build -t davialves1/bank-analytics:back-end .

echo "Pushing to Dockerhub"
docker push davialves1/bank-analytics:back-end

echo -e "${GREEN}Successfully updated backend image${NC}"
