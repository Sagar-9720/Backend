# Backend Rollout Plan

## Prerequisites
- Docker and/or Kubernetes installed
- All environment variables and secrets configured (see `k8s/secrets.yaml` and `k8s/configmap.yaml`)
- Database (Postgres) and MongoDB up and healthy

## Steps to Start Backend (Docker Compose)
1. Start databases and Eureka:
   ```sh
   docker-compose up -d postgres mongo eureka-server
   ```
2. Start all microservices and gateway:
   ```sh
   docker-compose up -d auth-service user-service trip-service gateway nginx
   ```
3. Check health endpoints for all services (e.g., `/actuator/health` for Spring Boot, `/health` for Node.js).

## Steps to Start Backend (Kubernetes)
1. Create namespace:
   ```sh
   kubectl apply -f k8s/namespace.yaml
   ```
2. Apply secrets and configmaps:
   ```sh
   kubectl apply -f k8s/secrets.yaml
   kubectl apply -f k8s/configmap.yaml
   ```
3. Deploy databases, Eureka, and all services:
   ```sh
   kubectl apply -f k8s/postgres.yaml
   kubectl apply -f k8s/mongo.yaml
   kubectl apply -f k8s/eureka-server.yaml
   kubectl apply -f k8s/auth-service.yaml
   kubectl apply -f k8s/user-service.yaml
   kubectl apply -f k8s/trip-service.yaml
   kubectl apply -f k8s/gateway.yaml
   kubectl apply -f k8s/nginx.yaml
   ```
4. Check pod and service status:
   ```sh
   kubectl get pods -n travel-mate
   kubectl get svc -n travel-mate
   ```

## Frontend Access
- Access the backend via Nginx on port 80/443.
- Gateway will route requests to the correct microservice using Eureka service discovery.
- Swagger UI and health endpoints are accessible for debugging.

---

## API Usage Guide

### User Registration & Authentication
- **Register:**
  - Endpoint: `POST /api/auth/register`
  - Example URL: `http://<your-domain-or-ip>/api/auth/register`
  - Payload: `{ "username": "yourname", "password": "yourpassword", ... }`
  - Response: Success or error message
- **Login:**
  - Endpoint: `POST /api/auth/login`
  - Example URL: `http://<your-domain-or-ip>/api/auth/login`
  - Payload: `{ "username": "yourname", "password": "yourpassword" }`
  - Response: JWT token and refresh token

### Trip Management
- **Create Trip:**
  - Endpoint: `POST /api/trips`
  - Auth: Bearer JWT token required
  - Payload: Trip details
- **Get Trips:**
  - Endpoint: `GET /api/trips`
  - Auth: Bearer JWT token required

### User Interactions
- **Comment on Trip:**
  - Endpoint: `POST /api/comments`
  - Auth: Bearer JWT token required
  - Payload: Comment details
- **Like a Trip:**
  - Endpoint: `POST /api/likes`
  - Auth: Bearer JWT token required
  - Payload: Like details
- **Save a Trip:**
  - Endpoint: `POST /api/saved-trips`
  - Auth: Bearer JWT token required
  - Payload: Trip ID

### Health & Debugging
- **Service Health:**
  - Endpoint: `/actuator/health` (Spring Boot services)
  - Endpoint: `/health` (Node.js user-service)
- **Swagger UI:**
  - Endpoint: `/swagger-ui/index.html` (for each service)

---

## Notes
- For production, review security, enable self-preservation in Eureka, and restrict Swagger/actuator endpoints.
- Monitor logs and health endpoints for troubleshooting.
- All requests from frontend should go through Nginx, which routes to gateway, then to the appropriate microservice.
- Use JWT tokens for authenticated requests; obtain them via the login API.
