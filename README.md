
## System Overview

Creation of a REST API in Spring Boot that will register bank branches and their locations. 

When performing a query, the user must return the distances to the registered bank branches.

### Main Technologies
- **Java 17** - Programming Language
- **Spring Boot 3.2.0** - Core Framework
- **Spring Data JPA** - Data Persistence
- **H2 Database** - In-Memory Database
- **Maven** - Dependency Management
- **Swagger/OpenAPI 3** - API Documentation

## Environment Requirements

### Minimum Versions
- Java 17 or higher
- Maven 3.8 or higher

### External Dependencies

- **H2 Database**: Starts automatically with the application
- **Mocks**: Used in unit tests (Mockito)

## Endpoints and Contracts

### Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html

### Main Endpoints

#### Register Agency
```http
POST /desafio/registrar
Content-Type: application/json

{
"posX": 10.0,
"posY": -5.0
}
```

**Validations:**
- `posX` and `posY` are required
- It is not allowed to register bank branches that are too close together

#### Search Nearby bank branches
```http
GET /desafio/distancia?posX=-10&posY=5
```

## Tests

### Test Execution

```bash
mvn test
```