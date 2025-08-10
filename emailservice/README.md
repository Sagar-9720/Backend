# EmailService Documentation

This service is responsible for sending emails (such as verification and password reset emails) for the TravelMate platform.

## Overview
- Built with Spring Boot.
- Used internally by other services (e.g., AuthService) to send transactional emails.
- Does not expose public REST API endpoints for external use.

## Features
- Sends verification emails to users during registration.
- Sends password reset emails.
- Can be extended to support other email notifications.

## Usage
- Other services interact with EmailService via internal service calls or message queues.
- No direct API usage by clients.

## Security
- Not exposed to the public; only accessible internally within the platform.

---

For more details, see the source code or contact the maintainers.

