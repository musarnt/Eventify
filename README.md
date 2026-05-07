# Eventify

Event and venue management platform built with Spring Boot.

## Tech Stack

- Java 21
- Spring Boot 4.0.6
- Lombok
- SpringDoc OpenAPI 3.0.3 (Swagger UI)
- JUnit 5 + Mockito

## Project Structure

    src/main/java/com/eventify
    ├── config/         # Beans, Seeder, Swagger and Exception Handler
    ├── controller/     # REST endpoints
    ├── model/          # Entities (Event, Venue)
    ├── repository/     # In-memory data layer
    └── service/        # Business logic and validations

## Getting Started

    # Clone the repository
    git clone https://github.com/musarnt/Eventify.git

    # Run the application
    ./mvnw spring-boot:run

## API Documentation

Once the app is running, access Swagger UI at:

    http://localhost:8080/swagger-ui/index.html

## Branch Strategy

This project follows Git Flow:

    main      → stable production-ready code
    develop   → integration branch
    feature/* → one branch per user story

## Commit Convention

This project follows Conventional Commits:

| Prefix | Usage |
|--------|-------|
| `feat` | New feature |
| `fix` | Bug fix |
| `chore` | Setup or config changes |
| `test` | Adding or updating tests |
| `docs` | Documentation only |