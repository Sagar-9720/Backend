# User-Service API Documentation

This service manages user-related features such as comments, likes, and saved trips for the TravelMate platform.

## Base URL
```
/api/user
```

## Endpoints

### Comments
- **POST /comments**
  - Create a new comment.
  - **Request:** JSON (comment details)
  - **Response:** Created comment object
  - **Security:** Typically requires authentication (see project middleware)

- **GET /comments**
  - Get all comments (optionally filtered).
  - **Response:** List of comments
  - **Security:** None or authentication (see project middleware)

- **GET /comments/:id**
  - Get a comment by ID.
  - **Response:** Comment object
  - **Security:** None or authentication

- **PUT /comments/:id**
  - Update a comment by ID.
  - **Request:** JSON (updated fields)
  - **Response:** Updated comment object
  - **Security:** Typically requires authentication

- **DELETE /comments/:id**
  - Delete a comment by ID.
  - **Response:** No content (204)
  - **Security:** Typically requires authentication

### Likes
- **POST /likes**
- **GET /likes**
- **GET /likes/:id**
- **PUT /likes/:id**
- **DELETE /likes/:id**
  - (Same structure as comments)

### Saved Trips
- **POST /saved-trips**
- **GET /saved-trips**
- **GET /saved-trips/:id**
- **PUT /saved-trips/:id**
- **DELETE /saved-trips/:id**
  - (Same structure as comments)

## Security
- Most endpoints require authentication (JWT) via middleware.
- Some endpoints may be public (see code for details).

## Error Handling
- Standard HTTP status codes and JSON error responses.

## Data Models
- See the `models` and `interfaces` directories for request/response model details.

---

For more details, see the source code or contact the maintainers.

