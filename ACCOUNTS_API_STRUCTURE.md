# Accounts API - Current Structure

## Project Structure

```
src/main/java/com/eazybytes/accounts/
+-- AccountsApplication.java
+-- constants/
¦   +-- UserConstants.java
+-- controller/
¦   +-- UserController.java
+-- dto/
¦   +-- UserRequestDto.java
¦   +-- UserResponseDto.java
+-- entity/
¦   +-- User.java
+-- repository/
¦   +-- UserRepository.java
+-- service/
    +-- UserService.java
    +-- impl/
        +-- UserServiceImpl.java
```

## API Endpoints

### 1. POST - Create User
**Endpoint:** `POST /api/create`

**Request Body:**
```json
{
  "name": "Jane Doe",
  "email": "jane@example.com"
}
```

**Response (201 Created):**
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "name": "Jane Doe",
  "email": "jane@example.com"
}
```

**Error Response (409 Conflict):**
- Returned when the email already exists.

## Key Features

1. Layered Architecture
- Controller -> Service -> Repository -> Entity

2. DTOs for API Communication
- `UserRequestDto` for input
- `UserResponseDto` for output

3. Validation
- Email uniqueness enforced in the service layer

4. Persistence
- JPA entity with UUID primary key

## Technologies Used

- Spring Boot
- Spring Data JPA
- Lombok
- Jakarta Persistence API
- Maven

## Running the Application

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

The application starts on `http://localhost:8000`

## Testing the API

```bash
curl -X POST http://localhost:8000/api/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "email": "jane@example.com"
  }'
```
