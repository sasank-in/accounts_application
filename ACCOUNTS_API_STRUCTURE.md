# Accounts API - Disciplined Structure

## Project Structure

```
src/main/java/com/eazybytes/accounts/
├── AccountsApplication.java                 # Main Spring Boot Application
├── constants/
│   └── AccountsConstants.java              # Constants for status codes and messages
├── controller/
│   └── AccountsController.java             # REST API endpoints
├── dto/
│   ├── UserDto.java                        # User Data Transfer Object
│   ├── ResponseDto.java                    # Standard Response DTO
│   ├── ErrorResponseDto.java               # Error Response DTO
│   └── AccountsDto.java                    # Accounts Data Transfer Object
├── entity/
│   ├── User.java                           # User Entity (Lombok)
│   ├── Account.java                        # Account Entity (Lombok)
│   ├── BaseEntity.java                     # Base Entity (if exists)
│   ├── UserRole.java                       # User Role Enum
│   └── AccountType.java                    # Account Type Enum
├── exception/
│   ├── GlobalExceptionHandler.java         # Global Exception Handler
│   ├── UserAlreadyExistsException.java     # Custom Exception
│   └── ResourceNotFoundException.java      # Custom Exception
├── mapper/
│   └── UserMapper.java                     # Entity to DTO Mapper
├── repository/
│   ├── UserRespository.java                # User JPA Repository
│   └── AccountsRepository.java             # Accounts JPA Repository
└── service/
    ├── IAccountsService.java               # Service Interface
    └── impl/
        └── AccountsServiceImpl.java         # Service Implementation
```

## API Endpoints

### 1. POST - Create Account
**Endpoint:** `POST /api/accounts/create`

**Request Body:**
```json
{
  "userName": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "9876543210"
}
```

**Response (201 Created):**
```json
{
  "statuscode": "201",
  "responseMsg": "Account created successfully"
}
```

**Error Response (400 Bad Request):**
```json
{
  "apiPath": "uri=/api/accounts/create",
  "errorCode": "BAD_REQUEST",
  "errorMessage": "User already registered with given phone number: 9876543210",
  "errorTime": "2024-02-26T10:30:00"
}
```

### 2. GET - Fetch Account Details
**Endpoint:** `GET /api/accounts/fetch?phoneNumber=9876543210`

**Response (200 OK):**
```json
{
  "userName": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "9876543210",
  "accountNumber": "1234567890",
  "accountType": "SAVINGS"
}
```

**Error Response (404 Not Found):**
```json
{
  "apiPath": "uri=/api/accounts/fetch",
  "errorCode": "NOT_FOUND",
  "errorMessage": "User not found with the given input data phoneNumber : '9876543210'",
  "errorTime": "2024-02-26T10:30:00"
}
```

## Key Features

1. **Disciplined Layered Architecture:**
   - Controller → Service → Repository → Entity
   - Clear separation of concerns

2. **DTOs for API Communication:**
   - UserDto for request/response
   - ResponseDto for success responses
   - ErrorResponseDto for error responses

3. **Exception Handling:**
   - Global exception handler
   - Custom exceptions for business logic
   - Proper HTTP status codes

4. **Mapper Utility:**
   - UserMapper for entity-to-DTO conversion
   - Reusable mapping logic

5. **Validation:**
   - Input validation using Jakarta validation annotations
   - Phone number format validation (10 digits)
   - Email validation

6. **Database:**
   - PostgreSQL with app_schema
   - Proper indexes on frequently queried columns
   - Foreign key constraints

## Technologies Used

- Spring Boot 3.5.11
- Spring Data JPA
- Lombok
- Jakarta Persistence API
- PostgreSQL
- Maven

## Running the Application

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

The application will start on `http://localhost:8000`

## Testing the APIs

### Create Account
```bash
curl -X POST http://localhost:8000/api/accounts/create \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "John Doe",
    "email": "john@example.com",
    "phoneNumber": "9876543210"
  }'
```

### Fetch Account
```bash
curl -X GET "http://localhost:8000/api/accounts/fetch?phoneNumber=9876543210" \
  -H "Content-Type: application/json"
```