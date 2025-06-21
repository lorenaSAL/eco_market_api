# EcoMarket API

This project uses **Maven**, with logging configured via **Logback**. It is a backend API that provides a variety of useful endpoints to handle user authentication, product management, and order processing.

## Key Features
- **JWT-based authentication**
- **CORS support**
- **JSON serialization** with Jackson

## Tools Used
- **Java 24**: Core programming language.
- **Jersey 4.0.0-M2**: RESTful API framework for building endpoints.
- **Jackson 3.0-rc5**: JSON serialization/deserialization with SNAKE_CASE strategy.
- **JJWT 0.12.6**: JSON Web Token for authentication.
- **Hibernate 8.0.2.Final**: Used for validating Java objects' fields and properties in the project. Its primary purpose is to enforce data integrity and business rules by applying constraints (e.g., `@NotNull`, `@Size`, `@Email`).
- **HikariCP 6.3.0**: Connection pooling for database access.
- **BCrypt 0.4**: Password hashing for secure authentication.
- **Maven**: Build tool for multi-module project management.
- **Logback 1.5.18**: Logging framework with SLF4J 2.0.17, configured in `server/src/main/resources/logback.xml`.
- **Grizzly**: HTTP server for running the Jersey application.

