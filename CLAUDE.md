# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.6 JWT authentication template designed as a reusable foundation for future projects. It implements a complete authentication system with JWT tokens, role-based access control, and MySQL database integration.

**Key Technologies:**
- Java 17
- Spring Boot 3.5.6 with Spring Security 6.x
- JWT (JJWT 0.12.6) for authentication
- MySQL 8.x with Flyway migrations
- Maven for dependency management
- Lombok for code reduction

## Essential Commands

### Build and Run
```bash
# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run

# Build deployable JAR
mvn clean package
```

### Database Operations
```bash
# Run Flyway migrations manually
mvn flyway:migrate

# Clean database (development only)
mvn flyway:clean
```

### Testing
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest

# Run tests with specific pattern
mvn test -Dtest=*ControllerTest
```

## Architecture Overview

The project follows a layered architecture pattern:

```
src/main/java/com/vildanden/auth_template/
├── config/          # Spring configurations (Security, JWT properties)
├── controller/      # REST endpoints (Auth, User, Role, System)
├── dto/            # Data Transfer Objects
│   ├── auth/       # Authentication DTOs (Login, Register, etc.)
│   ├── user/       # User management DTOs
│   ├── role/       # Role management DTOs
│   └── common/     # Shared DTOs (ApiResponse, PagedResponse)
├── entity/         # JPA entities (User, Role)
├── exception/      # Custom exceptions and global error handling
├── repository/     # JPA repositories
├── security/       # JWT implementation (Filter, Service, EntryPoint)
├── service/        # Business logic layer
└── util/          # Utilities and mappers
```

### Core Security Architecture

**JWT Implementation:**
- Separate access tokens (24h) and refresh tokens (7 days)
- Access tokens for API authentication, refresh tokens for token renewal
- Custom `JwtAuthenticationFilter` validates tokens on each request
- Role-based authorization with `@PreAuthorize` annotations

**Security Configuration (`SecurityConfig.java`):**
- Stateless session management
- Public endpoints: `/api/auth/*`, `/api/health`, `/api/info`
- Protected endpoints with role requirements:
  - `/api/admin/**` → ADMIN role
  - `/api/users/**` → USER, ADMIN, MODERATOR roles
  - `/api/roles/**` → ADMIN, MODERATOR roles

### Database Schema

**Core Tables:**
- `users`: User account information with security flags
- `roles`: System roles (ADMIN, USER, MODERATOR)
- `user_roles`: Many-to-many relationship table

**Default Data:**
- Admin user: username=`admin`, password=`admin123`, email=`admin@template.com`
- Three default roles: ADMIN, USER, MODERATOR

## Development Setup

### Database Configuration
The application expects a MySQL database. Update `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/auth_template_db
    username: root
    password: your_password
```

### JWT Configuration
JWT settings in `application.yml`:

```yaml
jwt:
  secret: your-secret-key-here  # Change in production
  expiration: 86400000          # 24 hours
  refresh-expiration: 604800000 # 7 days
```

### Running the Application
1. Ensure MySQL is running and database exists
2. Run `mvn spring-boot:run`
3. Application starts on `http://localhost:8080/api`
4. Flyway migrations run automatically on startup

## API Testing

The project includes an Insomnia collection at:
`src/main/java/com/vildanden/auth_template/docs/auth-template-insomnia.json`

**Quick Test Flow:**
1. POST `/api/auth/login` with admin credentials
2. Use returned `accessToken` as Bearer token for protected endpoints
3. Test user management endpoints with proper roles

## Key Implementation Patterns

### DTO Mapping
- Use `UserMapper` utility for entity-to-DTO conversions
- Separate request/response DTOs for clean API contracts
- `ApiResponseDTO<T>` for consistent response structure

### Exception Handling
- Global exception handler in `exception/` package
- Custom exceptions for business logic errors
- Consistent error response format

### Service Layer
- `AuthService`: Authentication logic, token management
- `UserService`: User CRUD operations, profile management
- `RoleService`: Role management for admin users

### Security Best Practices
- BCrypt password encoding with strength 12
- JWT token validation on every request
- Separate access/refresh token types to prevent misuse
- Account status validation (enabled, non-expired, non-locked)

## Common Development Tasks

### Adding New Endpoints
1. Create DTOs in appropriate `dto/` subdirectory
2. Add controller method with proper `@PreAuthorize` annotation
3. Implement service method with business logic
4. Add repository method if database access needed

### Adding New Roles
1. Insert role in database via migration or direct SQL
2. Update `SecurityConfig` role mappings if needed
3. Add role constants in entity classes

### Extending User Entity
1. Add fields to `User` entity
2. Create Flyway migration for database changes
3. Update DTOs and mappers accordingly
4. Modify registration/profile update logic

## Testing Notes

The project includes basic test structure:
- `AuthTemplateApplicationTests`: Basic Spring Boot integration test
- `AuthServiceTest`: Unit tests for authentication service

When writing tests, use Spring Boot test annotations and consider mocking external dependencies like JWT service for unit tests.