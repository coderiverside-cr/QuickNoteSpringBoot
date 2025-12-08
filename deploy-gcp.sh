#! /bin/bash

set -euo pipefail

# Configuration
export PROJECT_ID=quicknote-480515
export REGION=us-central1
export CONNECTION_NAME=quicknote-480515:us-central1:quicknote
export SERVICE_NAME=quicknote-api
export IMAGE_NAME=gcr.io/$PROJECT_ID/$SERVICE_NAME
export IMAGE_TAG=${CI_COMMIT_SHA:0:8}  # Use commit hash for image tagging, fallback to 'latest'
export DOCKER_TAG="${IMAGE_NAME}:${IMAGE_TAG:-latest}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${BLUE}ℹ ${1}${NC}"
}

log_success() {
    echo -e "${GREEN}✓ ${1}${NC}"
}

log_error() {
    echo -e "${RED}✗ ${1}${NC}"
}

# Validate environment
log_info "Validating environment..."
if ! command -v gcloud &> /dev/null; then
    log_error "gcloud CLI is not installed"
    exit 1
fi

if ! command -v docker &> /dev/null; then
    log_error "Docker is not installed"
    exit 1
fi

# Configure Docker authentication with GCR
log_info "Configuring Docker authentication..."
gcloud auth configure-docker --quiet

# Build the image
log_info "Building Spring Boot image with tag: ${DOCKER_TAG}..."
./mvnw clean package spring-boot:build-image \
    -Dspring-boot.build-image.imageName=${DOCKER_TAG} \
    -DskipTests \
    -q

log_success "Image built successfully"

# Push the image to GCR
log_info "Pushing image to GCR..."
docker push ${DOCKER_TAG}
log_success "Image pushed successfully"

# Deploy to Cloud Run
log_info "Deploying to Cloud Run..."
gcloud run deploy ${SERVICE_NAME} \
    --image ${DOCKER_TAG} \
    --region ${REGION} \
    --platform managed \
    --add-cloudsql-instances ${CONNECTION_NAME} \
    --project ${PROJECT_ID} \
    --allow-unauthenticated \
    --cpu 1 \
    --memory 1Gi \
    --timeout 300 \
    --max-instances 10 \
    --min-instances 1 \
    --set-env-vars INSTANCE_CONNECTION_NAME=${CONNECTION_NAME} \
    --set-env-vars DB_NAME=db \
    --set-env-vars DB_USER=postgres \
    --set-env-vars DB_PASSWORD='ultrasecret' \
    --set-env-vars JPA_DDL_AUTO=validate \
    --update-env-vars DEPLOYMENT_TIME="$(date -u +%Y-%m-%dT%H:%M:%SZ)" \
    --quiet

log_success "Deployment completed successfully!"

# Get service URL
SERVICE_URL=$(gcloud run services describe ${SERVICE_NAME} \
    --region ${REGION} \
    --project ${PROJECT_ID} \
    --format 'value(status.url)')

echo -e "\n${GREEN}Service deployed successfully!${NC}"
echo -e "Service URL: ${SERVICE_URL}"
echo -e "Region: ${REGION}"
echo -e "Image: ${DOCKER_TAG}\n"
