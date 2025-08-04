# Authentication and Authorization Implementation

This document describes the authentication and authorization implementation for the TripService microservice.

## Overview

The TripService uses a **JWT-based authentication system** that integrates with an external user authentication service. Instead of managing users locally, it delegates token verification to the dedicated user service.

## Architecture

### Components

1. **JwtAuthenticationFilter**: Intercepts incoming requests and validates JWT tokens
2. **UserAuthenticationService**: Communicates with the external user service for token validation
3. **SecurityConfig**: Configures Spring Security with role-based access control
4. **AuthenticationResponse**: Model for receiving user details from the authentication service
5. **SecurityUtils**: Utility class for accessing current user information

### Authentication Flow

1. Client sends request with `Authorization: Bearer <token>` header
2. `JwtAuthenticationFilter` extracts the token
3. Token is sent to user service at `/api/user-service/authenticate` endpoint
4. User service validates token and returns user details with roles
5. Spring Security context is populated with user information
6. Request proceeds with authorization checks

## Configuration

### Application Properties

```yaml
app:
  user-service:
    base-url: http://localhost:8080
    authenticate-endpoint: /api/user-service/authenticate
```

### Security Rules

| Endpoint Pattern | Required Role | Description |
|------------------|---------------|-------------|
| `/api/public/**` | None | Public endpoints |
| `/actuator/**` | None | Health check endpoints |
| `/countries/**` | USER, ADMIN | Country data |
| `/regions/**` | USER, ADMIN | Region data |
| `/tags/**` | USER, ADMIN | Tag management |
| `/api/destinations/**` | USER, ADMIN | Destination management |
| `/itineraries/**` | USER, ADMIN | Itinerary management |
| `/journals/public` | None | Public travel journals |
| `/journals/**` | USER, ADMIN | Travel journal management |
| `/api/trips/**` | USER, ADMIN | Trip management |

## Expected User Service Response

The user authentication service should return the following JSON structure:

```json
{
  "valid": true,
  "userId": "123",
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["USER"],
  "message": "Token is valid"
}
```

### Response Fields

- `valid`: Boolean indicating if the token is valid
- `userId`: Unique identifier for the user
- `username`: User's username
- `email`: User's email address
- `roles`: Array of role strings (e.g., "USER", "ADMIN")
- `message`: Status message

## Usage Examples

### Getting Current User Information

```java
// In your service or controller
String currentUsername = SecurityUtils.getCurrentUsername();
String currentUserId = SecurityUtils.getCurrentUserId();
boolean isAdmin = SecurityUtils.isAdmin();
AuthenticationResponse user = SecurityUtils.getCurrentUser();
```

### Method-Level Security

```java
@PreAuthorize("hasRole('ADMIN')")
public void adminOnlyMethod() {
    // This method can only be called by users with ADMIN role
}

@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public void userOrAdminMethod() {
    // This method can be called by users with USER or ADMIN role
}
```

## Error Handling

The system handles various authentication scenarios:

- **401 Unauthorized**: Invalid or expired token
- **403 Forbidden**: Valid token but insufficient permissions
- **Service Unavailable**: User authentication service is down

Error responses follow the standard `CustomResponseEntity` format:

```json
{
  "status": 401,
  "message": "Authentication failed",
  "data": null,
  "path": "/auth"
}
```

## Testing

### Valid Token Test

```bash
curl -H "Authorization: Bearer <valid-token>" \
     http://localhost:8082/api/trips
```

### Invalid Token Test

```bash
curl -H "Authorization: Bearer invalid-token" \
     http://localhost:8082/api/trips
```

### No Token Test

```bash
curl http://localhost:8082/api/trips
```

## Security Considerations

1. **Token Validation**: All tokens are validated with the external user service
2. **Stateless**: No session state is maintained in the application
3. **Role-Based Access**: Fine-grained access control based on user roles
4. **Public Endpoints**: Health checks and public data remain accessible
5. **Error Handling**: Secure error messages that don't leak sensitive information

## Integration with User Service

The TripService expects the user authentication service to:

1. Accept GET requests to `/api/user-service/authenticate`
2. Validate the Bearer token from the Authorization header
3. Return user details in the expected JSON format
4. Handle token expiration and invalid tokens appropriately

This design allows for centralized user management while keeping the TripService focused on its core business logic.
