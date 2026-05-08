# CodeInspire Backend

AI-powered personalized advisor system for computer science students.

## Tech Stack

- **Java 17+**
- **Spring Boot 3.x**
- **MyBatis Plus** - ORM
- **Spring Security + JWT** - Authentication
- **Redis** - Cache
- **Spring AI** - AI Integration
- **MySQL 8.x** - Database
- **WebSocket** - Real-time communication

## Project Structure

```
src/main/java/com/codeinspire/
├── CodeInspireApplication.java    # Main application class
├── config/                        # Configuration classes
├── controller/                    # REST controllers
├── service/                       # Business logic layer
├── service/impl/                  # Service implementations
├── repository/                    # Data access layer
├── entity/                        # Database entities
├── dto/                           # Data transfer objects
├── vo/                            # View objects
├── security/                      # Security & JWT
├── ai/                            # AI service integration
├── rag/                           # RAG (Retrieval Augmented Generation)
├── privacy/                       # Privacy masking
├── notification/                  # Notification system
├── feedback/                      # User feedback
├── abtest/                        # A/B testing
├── export/                        # Data export
├── websocket/                     # WebSocket configuration
└── exception/                     # Exception handling

src/main/resources/
├── mapper/                        # MyBatis XML mappers
└── application.yml                # Application configuration
```

## Getting Started

1. Install dependencies: `mvn clean install`
2. Configure database in `application.yml`
3. Run application: `mvn spring-boot:run`

## API Documentation

Access Swagger UI at: http://localhost:8080/api/swagger-ui.html
