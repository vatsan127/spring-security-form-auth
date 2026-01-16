# Spring Security Form-Based Authentication

A learning project demonstrating form-based authentication with Spring Security.

## Tech Stack

- Java 21
- Spring Boot 4.0.1
- Spring Security
- Spring Data JPA
- Spring Session JDBC
- PostgreSQL

## User Authentication Methods

### Method 1: Default User

With Spring Security dependency, a default user is created:
- **Username:** `user`
- **Password:** auto-generated, logged at application startup

### Method 2: Application Properties

```yaml
spring:
  security:
    user:
      name: "srivatsan"
      password: "password"
      roles: "ADMIN"
```

### Method 3: InMemoryUserDetailsManager Bean

Create a custom `InMemoryUserDetailsManager` bean for in-memory user storage.

**Class Hierarchy:**
```
UserDetailsService (Core interface which loads user-specific data)
        |
        V
UserDetailsManager (extends UserDetailsService)
  - Provides ability to create new users and update existing ones
        |
        V
InMemoryUserDetailsManager (implements UserDetailsManager)
  - Implementation backed by an in-memory map
```

**Password Encoding:**
- Default format: `{id}encoded_password`
- Without encoding: `{noop}plain_password`
- With BCrypt: `{bcrypt}encoded_password`

By default, `DelegatingPasswordEncoder` checks the format of stored password `{id}` (e.g., `{bcrypt}`), then passes the incoming password to `BCryptPasswordEncoder` for hashing and matching.

You can define a specific `PasswordEncoder` directly, bypassing `DelegatingPasswordEncoder`, so there is no need to put `{id}` in front of the password.

### Method 4: Database Storage (Used in this project)

Store username and password (hashed) in the database using Spring JPA.

**Implementation requires:**

1. **Entity class** (`UserAuthEntity`) implements `UserDetails`
   - Allows direct integration with Spring Security
   - Must implement `getUsername()`, `getPassword()`, `getAuthorities()`

2. **Service class** (`UserAuthEntityService`) implements `UserDetailsService`
   - Core method: `loadUserByUsername(String username)`
   - Called automatically by Spring Security during login
   - Fetches user from database via JPA repository
   - Throws `UsernameNotFoundException` if user not found

## Form-Based Authentication

It's a **stateful** authentication method. The server maintains the user's authentication state (aka Session) so that users don't have to provide username/password with each request.

**Flow:**
1. User enters credentials (username/password) in an HTML login form
2. On successful authentication, a session (`JSESSIONID`) is created to maintain authentication state across requests
3. With subsequent requests, the client only passes `JSESSIONID` (not username/password). The server validates it against the stored session

## Session Management

Spring Security provides comprehensive session management configuration.

**This project's configuration** (in `SecurityConfiguration.java`):

```java
.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
    .maximumSessions(1)
    .maxSessionsPreventsLogin(false)
)
```

### Key Configuration Options

| Option | Description |
|--------|-------------|
| `sessionCreationPolicy` | Controls when sessions are created (see Session Creation Policy below) |
| `maximumSessions` | Limits concurrent sessions per user (set to 1 in this project) |
| `maxSessionsPreventsLogin` | `false` (default): New login expires oldest session. `true`: New login is blocked until existing session expires |
| `invalidSessionUrl` | Redirect URL when an invalid session is detected |
| `sessionFixation` | Protection against session fixation attacks |

### Session Fixation Protection

| Strategy | Description |
|----------|-------------|
| `migrateSession` | Creates new session, copies attributes (DEFAULT) |
| `newSession` | Creates new session, does not copy attributes |
| `changeSessionId` | Changes session ID without creating new session |
| `none` | No protection (not recommended) |

## Session Creation Policy

| Policy | Description |
|--------|-------------|
| **IF_REQUIRED** | HttpSession is only created when needed (DEFAULT). For public APIs, no session is created. This is the appropriate choice for form-based authentication. |
| **ALWAYS** | HttpSession is always created. If already present, it is reused. |
| **NEVER** | Does not create a session, but uses one if present. Useful when session creation is handled externally. |
| **STATELESS** | No session is created. Used for stateless applications like REST APIs with JWT authentication. |

## Session Storage

By default, sessions are stored in-memory (lost on server restart).

This project uses **JDBC session storage** (configured in `application-security.yaml`):

```yaml
spring:
  session:
    store-type: jdbc
    jdbc:
      initialize-schema: always
```

**Benefits:**
- Sessions persist across application restarts
- Works in clustered/distributed environments
- Spring Session JDBC auto-creates required tables

## Disadvantages of Form-Based Authentication

1. **Security vulnerabilities:** Vulnerable to CSRF and session hijacking. By default, CSRF is enabled for form-based login and should not be disabled.
2. **Session management overhead:** In distributed systems, this can lead to scalability issues.
3. **Database load:** Multiple servers may require storing sessions in DB or cache, which requires memory and adds lookup time, causing latency issues.

## API Endpoints

### Login (GET)
Displays Spring Security's default login form
```
GET http://localhost:8080/spring-security-form-auth/login
```

### Login (POST)
Processes login form submission (handled by Spring Security)
```
POST http://localhost:8080/spring-security-form-auth/login
Form parameters: username, password
```

### Logout (POST)
Invalidates session and clears authentication
```
POST http://localhost:8080/spring-security-form-auth/logout
```
- Deletes JSESSIONID cookie
- Removes session from database (JDBC store)
- Redirects to `/login?logout`

### Register (POST)
Creates new user account
```
POST http://localhost:8080/spring-security-form-auth/register
Content-Type: application/json

{
    "username": "srivatsan",
    "password": "password",
    "role": "USER"
}
```

### Home (GET)
Welcome page (no authentication required)
```
GET http://localhost:8080/spring-security-form-auth/
```
