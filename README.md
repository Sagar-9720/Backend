# Travel-Mate Backend

This repository contains the backend services for the Travel-Mate application, a microservices-based travel platform. The backend is built using Java (Spring Boot), with supporting services for authentication, trip management, email notifications, and more.

## Architecture Overview

- **Microservices**: Each core feature is implemented as a separate Spring Boot service.
- **Service Discovery**: Eureka Server is used for service registration and discovery.
- **API Gateway**: Spring Cloud Gateway routes requests to the appropriate services.
- **Authentication**: JWT-based authentication managed by a dedicated Auth Service.
- **Database**: Each service manages its own data (database setup not included here).
- **Docker**: All services are containerized for easy deployment.

## Centralized Configuration Management

All microservices now use a centralized configuration approach via Spring Cloud Config Server. Configuration files for each service are stored in the `config-repo` directory. Each service supports three profiles:

- `local`: For local development (e.g., localhost endpoints)
- `docker`: For Docker Compose deployments (e.g., service discovery via container names)
- `prod`: For production deployments (e.g., environment variables, production settings)

Each service has the following configuration files in its `src/main/resources` directory:

- `application-local.yml`
- `application-docker.yml`
- `application-prod.yml`

## Switching Profiles

You can control which profile is active for each service by setting the `SPRING_PROFILES_ACTIVE` environment variable. In Docker Compose, this is set in the `docker-compose.yml` file for each service. Example:

```yaml
services:
  auth-service:
    environment:
      SPRING_PROFILES_ACTIVE: docker
  ...
```

Change the value to `local`, `docker`, or `prod` as needed.

## Config Server

- The `config-server` service serves configuration from the `config-repo` directory.
- All Spring Boot services are configured to fetch their configuration from the config server.

## Adding/Updating Configuration

- To update configuration for any service, edit the corresponding file in `config-repo`.
- For service-specific overrides, update the appropriate `application-<profile>.yml` in the service's `src/main/resources`.

## Services Implemented

- **authservice**: Handles user authentication, registration, and token validation.
- **tripservice**: Manages trips, itineraries, and travel journals. Includes endpoints for CRUD operations and user interactions.
- **emailservice**: Sends email notifications (e.g., for registration, password reset, trip updates).
- **eureka-server**: Service registry for microservice discovery.
- **gateway**: API gateway for routing and security.

## Key Features

- JWT authentication and token validation
- CRUD operations for trips, itineraries, and travel journals
- User interaction endpoints (like, comment, save, etc.)
- Custom response entities for consistent API responses
- Error handling with proper HTTP status codes (404, 400, etc.)
- Public/private journal support and search by tag
- Docker support for all services

## Project Structure

- `authservice/` - Authentication microservice
- `tripservice/` - Trip, itinerary, and journal management
- `emailservice/` - Email notification service
- `eureka-server/` - Service discovery
- `gateway/` - API gateway
- `nginx/` - (Optional) Nginx config for reverse proxy
- `scripts/` - Database and setup scripts

## How to Run

1. **Build all services**:
   ```sh
   ./gradlew build
   ```
2. **Start with Docker Compose**:
   ```sh
   docker-compose up --build
   ```
3. **Access Services**:
   - Eureka dashboard: `http://localhost:8761`
   - Gateway: `http://localhost:8080`

## Running with Docker Compose

1. Build all images:
   ```sh
   ./build-images.sh
   ```
2. Start the stack:
   ```sh
   docker-compose up --build
   ```
3. To change the active profile, edit the `SPRING_PROFILES_ACTIVE` variable in `docker-compose.yml` and restart the stack.

## Next Steps / TODO

- Add more integration and unit tests
- Implement user profile and social features
- Add monitoring and logging (Prometheus, Grafana)
- Improve documentation for each service

---

_This README summarizes the current state of the backend as of August 2025. For more details, see the README files in each service directory._
