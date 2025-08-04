# Authentication Service Migration

This document describes the restructuring of authentication from user-service to authservice.

## Changes Made

### 1. Created AuthService (Java/Spring Boot)
- **Location**: `/authservice`
- **Port**: 8083
- **Technology**: Java 21, Spring Boot 3.5.4, Spring Security, JPA, JWT
- **Database**: PostgreSQL (`travelmate_auth`)

#### Features Implemented:
- User registration
- User login  
- JWT token generation and validation
- Refresh token functionality
- Password encryption with BCrypt
- Role-based authentication
- Token validation endpoint for other services

#### Key Files:
- `AuthController.java` - REST endpoints for authentication
- `AuthService.java` - Business logic for authentication
- `User.java` & `Role.java` - JPA entities
- `JwtUtil.java` - JWT token utilities
- `SecurityConfig.java` - Spring Security configuration

### 2. Updated Gateway Configuration
- **File**: `/gateway/src/main/resources/application.yml`
- **Change**: Added route for auth-service at `/api/auth/**`

```yaml
- id: auth-service
  uri: http://localhost:8083
  predicates:
    - Path=/api/auth/**
```

### 3. Updated TripService Authentication
- **File**: `/tripservice/src/main/java/com/travelmate/tripservice/service/UserAuthenticationService.java`
- **Change**: Updated to call authservice instead of user-service for token validation
- **Endpoint Changed**: From `/api/user-service/authenticate` to `/api/auth/validate`

### 4. Cleaned User-Service 
- **Removed**: All authentication-related code
- **Deleted**: 
  - `auth.routes.ts` - Authentication routes
  - Authentication methods from `user.controller.ts` and `user.service.ts`
- **Created**: 
  - `userManagement.controller.ts` - Clean user management controller
  - `userManagement.service.ts` - Clean user management service
  - `userManagement.interface.ts` - Interface without auth methods

#### Remaining Features in User-Service:
- User profile management (CRUD)
- Profile image upload/delete
- Email change functionality
- Saved trips management
- Comments and likes
- Role management

## API Endpoints

### AuthService (Port 8083)
```
POST /api/auth/register     - User registration
POST /api/auth/login        - User login
POST /api/auth/logout       - User logout (client-side)
POST /api/auth/refresh      - Refresh access token
GET  /api/auth/validate     - Validate JWT token (used by other services)
```

### User-Service (Port 8081) - Non-Authentication Only
```
GET    /api/users           - Get all users
GET    /api/users/:id       - Get user by ID
PUT    /api/users/:id       - Update user
DELETE /api/users/:id       - Delete user
POST   /api/users/:id/profile-image   - Upload profile image
DELETE /api/users/:id/profile-image   - Delete profile image
PUT    /api/users/:id/change-email    - Change email
POST   /api/saved-trips     - Save trip/itinerary/destination
GET    /api/saved-trips/user/:user_id - Get user's saved items
...and other social features
```

### TripService (Port 8082) - Unchanged
All trip-related endpoints remain the same, but now authenticate via authservice.

## Architecture Flow

### Before (Authentication via User-Service):
```
Gateway -> User-Service (auth) 
        -> TripService -> User-Service (token validation)
```

### After (Authentication via AuthService):
```
Gateway -> AuthService (auth)
        -> User-Service (user management only)
        -> TripService -> AuthService (token validation)
```

## Benefits
1. **Separation of Concerns**: Authentication logic isolated from user management
2. **Scalability**: Auth service can be scaled independently
3. **Security**: Centralized JWT token management
4. **Consistency**: Single source of truth for authentication across all services
5. **Technology Flexibility**: Java-based auth service can leverage Spring Security ecosystem

## Database Setup
Create PostgreSQL database for authservice:
```sql
CREATE DATABASE travelmate_auth;
```

The authservice will auto-create tables and default roles (USER, ADMIN, GUEST) on startup.

## Environment Configuration

### AuthService (.env or application.properties):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/travelmate_auth
spring.datasource.username=postgres
spring.datasource.password=password
app.jwt.secret=your_jwt_secret_key_here_make_it_very_long_and_secure
app.jwt.refresh-secret=your_refresh_secret_key_here_make_it_very_long_and_secure
server.port=8083
```

### User-Service (remove JWT secrets):
Remove or comment out JWT-related environment variables as they're no longer needed.

## Migration Steps for Existing Data

If you have existing users in user-service database:
1. Export user data from user-service database
2. Transform data to match authservice schema
3. Import into authservice database
4. Update user references as needed

## Next Steps
1. Start authservice: `./gradlew bootRun` in `/authservice`
2. Update existing frontends to use `/api/auth/**` endpoints
3. Test token validation flow between services
4. Consider adding additional security features (password reset, email verification, etc.)
