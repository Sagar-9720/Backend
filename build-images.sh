#!/bin/bash

# Build and push Docker images for Travel Mate microservices

echo "🏗️ Building Travel Mate Docker images..."

# Set image tag (default to latest, can be overridden)
TAG=${1:-latest}
REGISTRY=${DOCKER_REGISTRY:-travel-mate}

# Function to build and tag image
build_image() {
    local service=$1
    local context=$2
    
    echo "🔨 Building $service..."
    docker build -t ${REGISTRY}/${service}:${TAG} $context
    
    if [ $? -eq 0 ]; then
        echo "✅ Successfully built ${REGISTRY}/${service}:${TAG}"
    else
        echo "❌ Failed to build ${REGISTRY}/${service}:${TAG}"
        exit 1
    fi
}

# Build all images
build_image "eureka-server" "./eureka-server"
build_image "auth-service" "./authservice"
build_image "user-service" "./user-service"
build_image "trip-service" "./tripservice"
build_image "gateway" "./gateway"

echo ""
echo "📦 Built images:"
docker images | grep ${REGISTRY}

echo ""
echo "🚀 To push images to registry, run:"
echo "docker push ${REGISTRY}/eureka-server:${TAG}"
echo "docker push ${REGISTRY}/auth-service:${TAG}"
echo "docker push ${REGISTRY}/user-service:${TAG}"
echo "docker push ${REGISTRY}/trip-service:${TAG}"
echo "docker push ${REGISTRY}/gateway:${TAG}"

echo ""
echo "✅ Build completed!"
