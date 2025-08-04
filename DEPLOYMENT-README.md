# Travel Mate Microservices Deployment Guide

This guide provides comprehensive instructions for deploying the Travel Mate microservices architecture using Docker containers, Kubernetes orchestration, and Eureka service discovery.

## 🏗️ Architecture Overview

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Load Balancer │────▶│     Gateway     │────▶│  Eureka Server  │
│     (Nginx)     │     │   (2 replicas)  │     │   (Discovery)   │
└─────────────────┘     └─────────────────┘     └─────────────────┘
                                 │
                    ┌────────────┼────────────┐
                    │            │            │
           ┌─────────▼───┐ ┌──────▼──────┐ ┌──▼─────────┐
           │ Auth Service│ │User Service │ │Trip Service│
           │ (1 replica) │ │(2 replicas) │ │(2 replicas)│
           └─────────────┘ └─────────────┘ └────────────┘
                    │            │            │
                    └────────────┼────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │     Databases           │
                    │  PostgreSQL + MongoDB   │
                    └─────────────────────────┘
```

## 📋 Prerequisites

- Docker Desktop or Docker Engine
- Kubernetes cluster (Docker Desktop, minikube, or cloud provider)
- kubectl CLI configured
- Node.js 20+ (for local development)
- Java 21+ (for local development)

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended for Development)

```bash
# Clone and navigate to the project
cd Travel-Mate/Backend

# Build all Docker images
./build-images.sh

# Start all services with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f

# Scale specific services
docker-compose up -d --scale user-service=3 --scale trip-service=3

# Stop all services
docker-compose down
```

### Option 2: Kubernetes Deployment (Production)

```bash
# Build and tag images for Kubernetes
./build-images.sh latest

# Deploy to Kubernetes
./k8s/deploy.sh

# Check deployment status
kubectl get pods -n travel-mate
kubectl get services -n travel-mate

# Access the application
kubectl port-forward service/gateway-service 8080:8080 -n travel-mate

# Cleanup
./k8s/cleanup.sh
```

## 🔧 Service Configuration

### Service Ports

| Service | Port | Replicas | Health Check |
|---------|------|----------|--------------|
| Gateway | 8080 | 2 | `/actuator/health` |
| Auth Service | 8083 | 1 | `/actuator/health` |
| User Service | 5000 | 2 | `/health` |
| Trip Service | 8082 | 2 | `/actuator/health` |
| Eureka Server | 8761 | 1 | `/actuator/health` |
| PostgreSQL | 5432 | 1 | N/A |
| MongoDB | 27017 | 1 | N/A |

### Environment Variables

Key environment variables for configuration:

```bash
# Database Configuration
POSTGRES_HOST=postgres-service
POSTGRES_PORT=5432
POSTGRES_USER=traveluser
POSTGRES_PASSWORD=password

# Service Discovery
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka/

# JWT Configuration
APP_JWT_SECRET=your_jwt_secret_key_here
APP_JWT_REFRESH_SECRET=your_refresh_secret_key_here
```

## 🐳 Docker Images

### Building Images

```bash
# Build all images
./build-images.sh

# Build specific image
docker build -t travel-mate/user-service:latest ./user-service

# Push to registry (configure DOCKER_REGISTRY first)
export DOCKER_REGISTRY=your-registry.com
./build-images.sh
docker push travel-mate/user-service:latest
```

### Image Security

All images use:
- Multi-stage builds for smaller size
- Non-root users for security
- Alpine Linux base images
- Health checks for reliability

## ☸️ Kubernetes Deployment

### Namespace and Resources

The deployment creates:
- `travel-mate` namespace
- ConfigMaps for configuration
- Secrets for sensitive data
- Persistent volumes for databases
- Services with load balancing
- Deployments with multiple replicas

### Load Balancing

- **Gateway**: 2 replicas with Kubernetes service load balancing
- **User Service**: 2 replicas with automatic load distribution
- **Trip Service**: 2 replicas with service mesh routing
- **External Access**: LoadBalancer service for gateway

### Service Discovery

Eureka Server provides:
- Service registration and discovery
- Health monitoring
- Load balancing metadata
- Service routing information

### Scaling

```bash
# Scale user service to 3 replicas
kubectl scale deployment user-service --replicas=3 -n travel-mate

# Scale trip service to 4 replicas  
kubectl scale deployment trip-service --replicas=4 -n travel-mate

# Auto-scaling (optional)
kubectl autoscale deployment user-service --cpu-percent=70 --min=2 --max=10 -n travel-mate
```

## 🔍 Monitoring and Health Checks

### Health Endpoints

- Gateway: `http://localhost:8080/actuator/health`
- Auth Service: `http://localhost:8083/actuator/health`
- User Service: `http://localhost:5000/health`
- Trip Service: `http://localhost:8082/actuator/health`
- Eureka: `http://localhost:8761/actuator/health`

### Eureka Dashboard

```bash
# Port forward to access Eureka dashboard
kubectl port-forward service/eureka-server-service 8761:8761 -n travel-mate

# Visit http://localhost:8761 to see registered services
```

### Checking Logs

```bash
# Docker Compose
docker-compose logs service-name

# Kubernetes
kubectl logs -f deployment/user-service -n travel-mate
kubectl logs -f deployment/trip-service -n travel-mate
```

## 🌐 Load Balancer Configuration

### Nginx Load Balancer

The included Nginx configuration provides:
- Round-robin load balancing
- Health checks
- Gzip compression
- Rate limiting
- SSL termination (configurable)

Access points:
- Main Application: `http://localhost` (port 80)
- Direct User Service: `http://localhost/direct/user/`
- Direct Trip Service: `http://localhost/direct/trip/`
- Eureka Dashboard: `http://localhost/eureka/`

## 🔒 Security Considerations

### Container Security
- Non-root users in all containers
- Multi-stage builds to reduce attack surface
- Regular base image updates
- Resource limits to prevent DoS

### Network Security
- Internal service communication only
- Gateway as single entry point
- Database access restricted to services
- Secrets management with Kubernetes secrets

### Authentication
- JWT tokens for service authentication
- Service-to-service communication secured
- Database credentials in secrets

## 🚦 Troubleshooting

### Common Issues

1. **Services not starting**:
   ```bash
   # Check pod status
   kubectl describe pod <pod-name> -n travel-mate
   
   # Check logs
   kubectl logs <pod-name> -n travel-mate
   ```

2. **Database connection issues**:
   ```bash
   # Check if database is running
   kubectl get pods -l app=postgres -n travel-mate
   
   # Test database connectivity
   kubectl exec -it <postgres-pod> -n travel-mate -- psql -U traveluser -d travelmate_auth
   ```

3. **Service discovery issues**:
   ```bash
   # Check Eureka server
   kubectl port-forward service/eureka-server-service 8761:8761 -n travel-mate
   # Visit http://localhost:8761
   ```

4. **Load balancer not accessible**:
   ```bash
   # Check service status
   kubectl get service gateway-service -n travel-mate
   
   # Get external IP
   kubectl get service gateway-service -n travel-mate -o jsonpath='{.status.loadBalancer.ingress[0].ip}'
   ```

### Performance Optimization

1. **Resource Tuning**:
   ```yaml
   resources:
     requests:
       memory: "256Mi"
       cpu: "250m"
     limits:
       memory: "512Mi"
       cpu: "500m"
   ```

2. **JVM Tuning** (for Java services):
   ```bash
   JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
   ```

3. **Connection Pool Tuning**:
   ```yaml
   spring:
     datasource:
       hikari:
         maximum-pool-size: 10
         connection-timeout: 20000
   ```

## 📈 Production Considerations

### High Availability
- Deploy across multiple availability zones
- Use managed databases (RDS, Cloud SQL)
- Implement circuit breakers
- Set up monitoring and alerting

### Backup and Recovery
- Regular database backups
- Configuration backup
- Disaster recovery procedures
- Test restore procedures

### Monitoring
- Implement Prometheus and Grafana
- Set up ELK stack for logging
- Use distributed tracing (Jaeger/Zipkin)
- Configure alerting rules

## 🔄 CI/CD Integration

### Example GitHub Actions

```yaml
name: Deploy to Kubernetes
on:
  push:
    branches: [main]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Build and push images
        run: ./build-images.sh ${{ github.sha }}
      - name: Deploy to Kubernetes
        run: ./k8s/deploy.sh
```

## 📞 Support

For issues and questions:
1. Check the troubleshooting section
2. Review logs for error messages
3. Verify network connectivity
4. Check resource constraints
5. Validate configuration files

## 🏆 Best Practices

1. **Use specific image tags** instead of `latest` in production
2. **Implement health checks** for all services
3. **Set resource limits** to prevent resource starvation
4. **Use secrets** for sensitive configuration
5. **Monitor** service performance and logs
6. **Regular updates** of base images and dependencies
7. **Test** deployments in staging environment first
