#!/bin/bash

echo "Stopping all running containers..."
# Try to gracefully stop containers first
docker stop $(docker ps -a -q) 2>/dev/null || echo "No running containers to stop gracefully"

# Force kill any containers that didn't stop gracefully
running_containers=$(docker ps -q)
if [ -n "$running_containers" ]; then
  echo "Some containers are still running. Force killing them..."
  docker kill $(docker ps -q) 2>/dev/null || echo "Failed to kill some containers"
fi

echo "Removing all containers..."
# Force remove all containers
docker rm -f $(docker ps -a -q) 2>/dev/null || echo "No containers to remove or failed to remove some"

echo "Removing all images..."
# Use force flag
docker rmi $(docker images -a -q) --force 2>/dev/null || echo "No images to remove or failed to remove some"

echo "Removing all volumes..."
docker volume rm $(docker volume ls -q) 2>/dev/null || echo "No volumes to remove"

echo "Removing all networks (except default ones)..."
docker network prune -f

echo "Removing build cache..."
docker builder prune -a -f

echo "Docker system has been cleared!"
docker system df
