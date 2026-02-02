#!/bin/bash

# Configuration
PROJECT_ID="YOUR_PROJECT_ID"
REGION="asia-south1"
REGISTRY="asia-south1-docker.pkg.dev"

echo "========================================="
echo "Deploying Infrastructure Platform to GCP"
echo "========================================="

# Get Redis host
echo "Getting Redis host..."
REDIS_HOST=$(gcloud redis instances describe tutoring-redis --region=$REGION --format="get(host)")
echo "Redis host: $REDIS_HOST"

# Service 1: Eureka
echo ""
echo "========================================="
echo "Deploying Service Discovery (Eureka)"
echo "========================================="
cd service-discovery
mvn clean package -DskipTests
docker build -t $REGISTRY/$PROJECT_ID/tutoring-platform/service-discovery:v1 .
docker push $REGISTRY/$PROJECT_ID/tutoring-platform/service-discovery:v1
gcloud run deploy service-discovery \
  --image=$REGISTRY/$PROJECT_ID/tutoring-platform/service-discovery:v1 \
  --platform=managed \
  --region=$REGION \
  --allow-unauthenticated \
  --port=8761 \
  --memory=512Mi \
  --min-instances=1 \
  --set-env-vars="SPRING_PROFILES_ACTIVE=gcp"

EUREKA_URL=$(gcloud run services describe service-discovery --region=$REGION --format="value(status.url)")/eureka/
echo "Eureka URL: $EUREKA_URL"

cd ..

# Service 2: Config Server
echo ""
echo "========================================="
echo "Deploying Config Server"
echo "========================================="
cd config-server
mvn clean package -DskipTests
docker build -t $REGISTRY/$PROJECT_ID/tutoring-platform/config-server:v1 .
docker push $REGISTRY/$PROJECT_ID/tutoring-platform/config-server:v1
gcloud run deploy config-server \
  --image=$REGISTRY/$PROJECT_ID/tutoring-platform/config-server:v1 \
  --platform=managed \
  --region=$REGION \
  --allow-unauthenticated \
  --port=8888 \
  --memory=512Mi \
  --min-instances=1 \
  --set-env-vars="SPRING_PROFILES_ACTIVE=gcp,EUREKA_URL=$EUREKA_URL"

CONFIG_URL=$(gcloud run services describe config-server --region=$REGION --format="value(status.url)")
echo "Config Server URL: $CONFIG_URL"

cd ..

# Service 3: API Gateway
echo ""
echo "========================================="
echo "Deploying API Gateway"
echo "========================================="
cd api-gateway
mvn clean package -DskipTests
docker build -t $REGISTRY/$PROJECT_ID/tutoring-platform/api-gateway:v1 .
docker push $REGISTRY/$PROJECT_ID/tutoring-platform/api-gateway:v1
gcloud run deploy api-gateway \
  --image=$REGISTRY/$PROJECT_ID/tutoring-platform/api-gateway:v1 \
  --platform=managed \
  --region=$REGION \
  --allow-unauthenticated \
  --port=8080 \
  --memory=1Gi \
  --min-instances=1 \
  --max-instances=10 \
  --vpc-connector=tutoring-connector \
  --set-env-vars="SPRING_PROFILES_ACTIVE=gcp,REDIS_HOST=$REDIS_HOST,EUREKA_URL=$EUREKA_URL,JWT_SECRET=your-jwt-secret-here"

API_GATEWAY_URL=$(gcloud run services describe api-gateway --region=$REGION --format="value(status.url)")
echo "API Gateway URL: $API_GATEWAY_URL"

cd ..

echo ""
echo "========================================="
echo "Deployment Complete!"
echo "========================================="
echo "Eureka Dashboard: $EUREKA_URL"
echo "Config Server: $CONFIG_URL"
echo "API Gateway: $API_GATEWAY_URL"
echo ""
echo "Test API Gateway health:"
echo "curl $API_GATEWAY_URL/actuator/health"
