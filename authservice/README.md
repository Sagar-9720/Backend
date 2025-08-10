# AuthService API Documentation

This service handles authentication, user management, and role management for the TravelMate platform.

## Base URL
```
/api/auth
```

## Endpoints

### 1. User Registration & Authentication

- **POST /register**
  - Registers a new user.
  - **Request:** `RegisterRequest` JSON (name, email, phone, dob, password, gender)
  - **Response:** `AuthResponse` (token, refreshToken, user info)
  - **Security:** None

- **POST /login**
  - Authenticates a user.
  - **Request:** `LoginRequest` JSON (email, password)
  - **Response:** `AuthResponse` (token, refreshToken, user info)
  - **Security:** None

- **POST /register-subadmin**
  - Registers a sub-admin (ADMIN only).
  - **Request:** `RegisterRequest` JSON
  - **Headers:** `Authorization: Bearer <token>` (ADMIN)
  - **Response:** `AuthResponse`
  - **Security:** Bearer token (ADMIN)

### 2. Token & Session Management

- **GET /validate**
  - Validates a JWT token.
  - **Headers:** `Authorization: Bearer <token>`
  - **Response:** `TokenValidationResponse`
  - **Security:** Bearer token

- **POST /refresh**
  - Refreshes JWT tokens.
  - **Request:** `RefreshTokenRequest` JSON (refreshToken)
  - **Response:** `AuthResponse`
  - **Security:** None

- **POST /logout**
  - Logs out a user.
  - **Headers:** `Authorization: Bearer <token>`
  - **Response:** `LogoutResponse`
  - **Security:** Bearer token

### 3. Email Verification & Password Reset

- **GET /verify-email?token=...**
  - Verifies a user's email.
  - **Response:** `AuthResponse`
  - **Security:** None

- **POST /resend-verification**
  - Resends verification email.
  - **Request:** `EmailRequest` JSON (to)
  - **Response:** `AuthResponse`
  - **Security:** None

- **POST /initiate-password-reset**
  - Initiates password reset.
  - **Request:** `EmailRequest` JSON (to)
  - **Response:** `AuthResponse`
  - **Security:** None

- **POST /reset-password**
  - Resets password using token.
  - **Request:** `UserUpdateInfoRequest` JSON (token, password)
  - **Response:** `AuthResponse`
  - **Security:** None

### 4. User Management

- **PUT /update-user**
  - Updates user info.
  - **Request:** `UserUpdateInfoRequest` JSON
  - **Response:** `UserInfoDTO`
  - **Security:** Bearer token

- **PUT /change-password**
  - Changes user password.
  - **Request:** `UserUpdateInfoRequest` JSON (userId, oldPassword, password)
  - **Response:** `UserInfoDTO`
  - **Security:** Bearer token

- **DELETE /delete-user/{userId}**
  - Deletes a user (ADMIN only).
  - **Headers:** `Authorization: Bearer <token>`
  - **Response:** `UserInfoDTO`
  - **Security:** Bearer token (ADMIN)

- **GET /user-info**
  - Gets current user info.
  - **Headers:** `Authorization: Bearer <token>`
  - **Response:** `UserInfoDTO`
  - **Security:** Bearer token

- **GET /all-users**
  - Gets all users (ADMIN only).
  - **Headers:** `Authorization: Bearer <token>`
  - **Response:** `List<UserInfoDTO>`
  - **Security:** Bearer token (ADMIN)

- **PUT /update-role/{role}**
  - Updates a user's role (ADMIN only).
  - **Request:** `UserUpdateInfoRequest` JSON
  - **Response:** `UserInfoDTO`
  - **Security:** Bearer token (ADMIN)

- **GET /check-email/{email}**
  - Checks if an email exists.
  - **Response:** `UserInfoDTO`
  - **Security:** None

### 5. Role Management

- **GET /roles**
  - Gets all roles.
  - **Response:** `List<Role>`
  - **Security:** None

- **POST /roles**
  - Creates a new role (ADMIN/SUBADMIN).
  - **Headers:** `Authorization: Bearer <token>`
  - **Request:** `Role` JSON
  - **Response:** `Role`
  - **Security:** Bearer token (ADMIN/SUBADMIN)

- **PUT /roles/{roleId}**
  - Updates a role (ADMIN/SUBADMIN).
  - **Headers:** `Authorization: Bearer <token>`
  - **Request:** `Role` JSON
  - **Response:** `Role`
  - **Security:** Bearer token (ADMIN/SUBADMIN)

- **DELETE /roles/{roleId}**
  - Deletes a role (ADMIN only).
  - **Headers:** `Authorization: Bearer <token>`
  - **Response:** Success message
  - **Security:** Bearer token (ADMIN)

## Security
- Most endpoints require a Bearer JWT token in the `Authorization` header.
- Role-based access enforced for sensitive operations (e.g., only ADMIN can delete users/roles).

## Error Handling
- All responses are wrapped in `CustomResponseEntity` with status, message, and data.
- Standard HTTP status codes are used.

## Data Models
- See the `dto` and `entity` packages for request/response model details.

---

For more details, see the source code or contact the maintainers.

