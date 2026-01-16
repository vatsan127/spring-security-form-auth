# Spring Security Form-Based Authentication

A learning project demonstrating form-based authentication with Spring Security.

## Tech Stack

- Java 21
- Spring Boot 4.0.1
- Spring Security
- Spring Data JPA
- Spring Session JDBC
- PostgreSQL
- Lombok

## Project Structure

```
src/main/java/com/github/spring_security_form_auth/
├── config/SecurityConfiguration.java       # Security filter chain, password encoder
├── controller/UserAuthController.java      # REST endpoints
├── entity/UserAuthEntity.java              # JPA entity (implements UserDetails)
├── repository/UserAuthEntityRepository.java
├── service/UserAuthEntityService.java      # Implements UserDetailsService
└── SpringSecurityFormAuthApplication.java
```

## Key Design Decisions

- `UserAuthEntity` implements `UserDetails` for direct Spring Security integration
- `UserAuthEntityService` implements `UserDetailsService` for database user lookup
- BCrypt used for password hashing
- Sessions stored in JDBC (persists across restarts)
- CSRF disabled for learning purposes (not production-ready)

## Endpoints

| Endpoint | Method | Auth Required | Description |
|----------|--------|---------------|-------------|
| `/auth/register` | POST | No | Register new user (JSON: username, password, role) |
| `/` | GET | No | Welcome page |
| `/login` | GET | No | Spring Security default login form |
| `/logout` | POST | Yes | Logout handler |

## Authentication Flow

1. Register: `POST /auth/register` with `{"username": "x", "password": "y", "role": "USER"}`
2. Login: Navigate to `/login`, submit credentials via form
3. Spring Security calls `UserAuthEntityService.loadUserByUsername()`
4. BCrypt verifies password against stored hash
5. Session created and stored in database (JSESSIONID cookie)

## Database

- PostgreSQL: `localhost:5432/user_db`
- Table: `user_auth` (id, user_name, password, role)
- Session tables auto-created by Spring Session JDBC

## Configuration Profiles

- `postgres` - Database connection settings
- `security` - Session timeout (5m), JDBC session storage

## Running the Application

```bash
# Ensure PostgreSQL is running with database 'user_db'
mvn spring-boot:run
```

Application runs at: `http://localhost:8080/spring-security-form-auth/`

## Notes

See `Notes.txt` for additional learning notes about form-based authentication disadvantages and alternatives.
