# Travel-Mate Backend

This repository contains the backend services for the Travel-Mate application, a microservices-based travel platform. The backend is built using Java (Spring Boot), with supporting services for authentication, trip management, email notifications, and more.

## Architecture Overview

- **Microservices**: Each core feature is implemented as a separate Spring Boot service.
- **Service Discovery**: Eureka Server is used for service registration and discovery.
- **API Gateway**: Spring Cloud Gateway routes requests to the appropriate services.
- **Authentication**: JWT-based authentication managed by a dedicated Auth Service.
- **Database**: Each service manages its own data (database setup not included here).
- **Docker**: All services are containerized for easy deployment.

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

## Next Steps / TODO

- Add more integration and unit tests
- Implement user profile and social features
- Add monitoring and logging (Prometheus, Grafana)
- Improve documentation for each service

---

_This README summarizes the current state of the backend as of August 2025. For more details, see the README files in each service directory._
