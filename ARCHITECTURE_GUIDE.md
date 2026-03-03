# Accounts Microservice - Architecture & Implementation Guide

## Table of Contents
1. [Project Overview](#project-overview)
2. [Package Structure](#package-structure)
3. [Data Flow](#data-flow)
4. [Detailed Component Explanation](#detailed-component-explanation)
5. [Step-by-Step Usage Flow](#step-by-step-usage-flow)
6. [Best Practices](#best-practices)

---

## Project Overview

This is a Spring Boot microservice for managing user accounts. It follows a **layered architecture** pattern with clear separation of concerns:

```
Request → Controller → Service → Repository → Database
   ↓
Response ← Mapper ← Entity ← DTO
```

---

## Package Structure

### 1. **entity/** - Database Models
**Location:** `src/main/java/com/eazybytes/accounts/entity/`

**Purpose:** Contains JPA entities that map to database tables.

**Files:**
- **User.java** - Represents a user in the system
  - Fields: id, username, email, passwordHash, phoneNumber, role, active, verified, createdAt, updatedAt
  - Relationships: One-to-Many with Account
  - Annotations: @Entity, @Table, Lombok (@Getter, @Setter, @Builder)

- **Account.java** - Represents a bank account
  - Fields: id, accountNumber, accountType, balance, currency, active, createdAt, updatedAt, user
  - Relationships: Many-to-One with User
  - Annotations: @Entity, @Table, @ManyToOne, @JoinColumn

- **UserRole.java** - Enum for user roles
  - Values: USER, ADMIN, MANAGER
  - Used for role-based access control

- **AccountType.java** - Enum for account types
  - Values: SAVINGS, CURRENT
  - Defines the type of account

- **BaseEntity.java** - (Optional) Base class for common fields
  - Can contain shared fields like id, createdAt, updatedAt

**Use Case:**
- Entities are the source of truth for database schema
- They define relationships between tables
- Lombok annotations reduce boilerplate code

---

### 2. **dto/** - Data Transfer Objects
**Location:** `src/main/java/com/eazybytes/accounts/dto/`

**Purpose:** Transfer data between layers without exposing entities directly.

**Files:**
- **UserDto.java** - DTO for user data in API requests/responses
  - Fields: userName, email, phoneNumber, accountNumber, accountType
  - Annotations: @Data (Lombok), @NotEmpty, @Email, @Pattern (validation)
  - Why separate from entity? Hide sensitive fields like passwordHash

- **ResponseDto.java** - Standard success response wrapper
  - Fields: statuscode, responseMsg
  - Used for all successful API responses
  - Example: `{"statuscode": "201", "responseMsg": "Account created successfully"}`

- **ErrorResponseDto.java** - Standard error response wrapper
  - Fields: apiPath, errorCode, errorMessage, errorTime
  - Used for all error responses
  - Provides consistent error format

- **AccountsDto.java** - DTO for account data
  - Fields: accountNumber, accountType, branchAddress, customerId
  - Used when account details need to be returned

**Use Case:**
- Decouples API contracts from database schema
- Allows validation at API boundary
- Hides internal implementation details

---

### 3. **repository/** - Data Access Layer
**Location:** `src/main/java/com/eazybytes/accounts/repository/`

**Purpose:** Provides database access methods using Spring Data JPA.

**Files:**
- **UserRespository.java** - JPA Repository for User entity
  ```java
  public interface UserRespository extends JpaRepository<User, UUID> {
      Optional<User> findByPhoneNumber(String phoneNumber);
  }
  ```
  - Extends JpaRepository for CRUD operations
  - Custom method: findByPhoneNumber() - queries by phone number
  - Spring Data generates SQL automatically

- **AccountsRepository.java** - JPA Repository for Account entity
  ```java
  public interface AccountsRepository extends JpaRepository<Account, Long> {
      Optional<Account> findByUserId(UUID userId);
  }
  ```
  - Custom method: findByUserId() - finds account by user ID
  - Used to fetch account details for a specific user

**Use Case:**
- Abstracts database operations
- No SQL writing needed - Spring Data generates it
- Easy to test with mock repositories

---

### 4. **service/** - Business Logic Layer
**Location:** `src/main/java/com/eazybytes/accounts/service/`

**Purpose:** Contains business logic and orchestrates repository calls.

**Files:**
- **IAccountsService.java** - Service interface (contract)
  ```java
  public interface IAccountsService {
      void createAccount(UserDto userDto);
      UserDto fetchAccount(String phoneNumber);
  }
  ```
  - Defines what operations are available
  - Implementation can be swapped without affecting consumers

- **impl/AccountsServiceImpl.java** - Service implementation
  ```java
  @Service
  @AllArgsConstructor
  public class AccountsServiceImpl implements IAccountsService {
      private final AccountsRepository accountsRepository;
      private final UserRespository userRepository;
      
      @Override
      public void createAccount(UserDto userDto) {
          // 1. Validate user doesn't exist
          // 2. Create User entity
          // 3. Save to database
          // 4. Create Account entity
          // 5. Save to database
      }
  }
  ```
  - Implements business logic
  - Calls repositories for data access
  - Handles validation and error cases
  - Manages transactions

**Use Case:**
- Centralizes business rules
- Reusable across multiple controllers
- Easy to unit test

---

### 5. **controller/** - API Layer
**Location:** `src/main/java/com/eazybytes/accounts/controller/`

**Purpose:** Handles HTTP requests and responses.

**Files:**
- **AccountsController.java** - REST controller
  ```java
  @RestController
  @RequestMapping(path = "/api/accounts")
  @AllArgsConstructor
  public class AccountsController {
      private final IAccountsService accountsService;
      
      @PostMapping("/create")
      public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody UserDto userDto) {
          accountsService.createAccount(userDto);
          return ResponseEntity.status(HttpStatus.CREATED)
              .body(new ResponseDto("201", "Account created successfully"));
      }
      
      @GetMapping("/fetch")
      public ResponseEntity<UserDto> fetchAccountDetails(@RequestParam String phoneNumber) {
          UserDto userDto = accountsService.fetchAccount(phoneNumber);
          return ResponseEntity.status(HttpStatus.OK).body(userDto);
      }
  }
  ```
  - Maps HTTP endpoints to service methods
  - Validates input using @Valid
  - Returns appropriate HTTP status codes
  - Handles request/response conversion

**Use Case:**
- Entry point for API requests
- Validates input
- Converts between DTOs and service calls

---

### 6. **mapper/** - Data Conversion Utility
**Location:** `src/main/java/com/eazybytes/accounts/mapper/`

**Purpose:** Converts between entities and DTOs.

**Files:**
- **UserMapper.java** - Maps User entity ↔ UserDto
  ```java
  public class UserMapper {
      public static UserDto mapToUserDto(User user, UserDto userDto) {
          userDto.setUserName(user.getUsername());
          userDto.setEmail(user.getEmail());
          userDto.setPhoneNumber(user.getPhoneNumber());
          return userDto;
      }
      
      public static User mapToUser(UserDto userDto, User user) {
          user.setUsername(userDto.getUserName());
          user.setEmail(userDto.getEmail());
          user.setPhoneNumber(userDto.getPhoneNumber());
          user.setPasswordHash("defaultPassword123");
          return user;
      }
  }
  ```
  - Converts entity to DTO for API responses
  - Converts DTO to entity for database storage
  - Keeps mapping logic centralized

**Use Case:**
- Reusable conversion logic
- Separates concerns between layers
- Easy to maintain and test

---

### 7. **exception/** - Error Handling
**Location:** `src/main/java/com/eazybytes/accounts/exception/`

**Purpose:** Custom exceptions and global error handling.

**Files:**
- **UserAlreadyExistsException.java** - Custom exception
  ```java
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public class UserAlreadyExistsException extends RuntimeException {
      public UserAlreadyExistsException(String message) {
          super(message);
      }
  }
  ```
  - Thrown when user tries to create duplicate account
  - Returns 400 Bad Request

- **ResourceNotFoundException.java** - Custom exception
  ```java
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public class ResourceNotFoundException extends RuntimeException {
      public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
          super(String.format("%s not found with the given input data %s : '%s'", 
              resourceName, fieldName, fieldValue));
      }
  }
  ```
  - Thrown when resource is not found
  - Returns 404 Not Found

- **GlobalExceptionHandler.java** - Centralized exception handling
  ```java
  @ControllerAdvice
  public class GlobalExceptionHandler {
      @ExceptionHandler(UserAlreadyExistsException.class)
      public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistsException(...) {
          // Return formatted error response
      }
  }
  ```
  - Catches all exceptions globally
  - Returns consistent error format
  - Logs errors for debugging

**Use Case:**
- Consistent error responses
- Centralized error handling
- Better debugging and monitoring

---

### 8. **constants/** - Application Constants
**Location:** `src/main/java/com/eazybytes/accounts/constants/`

**Purpose:** Centralized constants used across the application.

**Files:**
- **AccountsConstants.java** - Constants
  ```java
  public class AccountsConstants {
      public static final String STATUS_201 = "201";
      public static final String MESSAGE_201 = "Account created successfully";
      public static final String STATUS_200 = "200";
      public static final String MESSAGE_200 = "Request processed successfully";
      public static final String STATUS_500 = "500";
      public static final String MESSAGE_500 = "An error occurred...";
  }
  ```
  - Centralized status codes and messages
  - Avoids magic strings in code
  - Easy to maintain and update

**Use Case:**
- Single source of truth for constants
- Easy to change values globally
- Improves code maintainability

---

## Data Flow

### Flow Diagram
```
┌─────────────────────────────────────────────────────────────┐
│                    HTTP Request                              │
│              POST /api/accounts/create                       │
│         {"userName": "John", "email": "..."}                │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
        ┌────────────────────────────┐
        │   AccountsController       │
        │  - Validates input         │
        │  - Calls service           │
        └────────────┬───────────────┘
                     │
                     ▼
        ┌────────────────────────────┐
        │  IAccountsService          │
        │  (Interface)               │
        └────────────┬───────────────┘
                     │
                     ▼
        ┌────────────────────────────┐
        │ AccountsServiceImpl         │
        │ - Business logic           │
        │ - Validation               │
        │ - Calls repositories       │
        └────────────┬───────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
        ▼                         ▼
    ┌─────────────┐         ┌──────────────┐
    │ UserMapper  │         │ UserRepository
    │ - Converts  │         │ - Queries DB
    │   DTO→Entity│         │ - Saves User
    └─────────────┘         └──────────────┘
        │                         │
        ▼                         ▼
    ┌─────────────┐         ┌──────────────┐
    │ User Entity │         │ Database     │
    │ - Mapped    │         │ - Persisted  │
    └─────────────┘         └──────────────┘
                     │
                     ▼
        ┌────────────────────────────┐
        │ AccountsRepository         │
        │ - Creates Account          │
        │ - Saves to DB              │
        └────────────┬───────────────┘
                     │
                     ▼
        ┌────────────────────────────┐
        │ ResponseDto                │
        │ - Status: 201              │
        │ - Message: Success         │
        └────────────┬───────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                   HTTP Response                              │
│                      201 Created                             │
│         {"statuscode": "201", "responseMsg": "..."}         │
└─────────────────────────────────────────────────────────────┘
```

---

## Step-by-Step Usage Flow

### Scenario: Create a New Account

#### Step 1: Client sends HTTP Request
```bash
POST /api/accounts/create
Content-Type: application/json

{
  "userName": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "9876543210"
}
```

#### Step 2: Controller receives request
```java
@PostMapping("/create")
public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody UserDto userDto) {
    // UserDto is validated here (@Valid annotation)
    // If validation fails, 400 Bad Request is returned
    accountsService.createAccount(userDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ResponseDto("201", "Account created successfully"));
}
```

#### Step 3: Service processes business logic
```java
@Override
public void createAccount(UserDto userDto) {
    // Step 3a: Check if user already exists
    Optional<User> optionalUser = userRepository.findByPhoneNumber(userDto.getPhoneNumber());
    if (optionalUser.isPresent()) {
        throw new UserAlreadyExistsException("User already registered...");
    }
    
    // Step 3b: Map DTO to Entity
    User user = UserMapper.mapToUser(userDto, new User());
    user.setRole(UserRole.USER);
    
    // Step 3c: Save User to database
    User savedUser = userRepository.save(user);
    
    // Step 3d: Create Account entity
    Account account = createNewAccount(savedUser);
    
    // Step 3e: Save Account to database
    accountsRepository.save(account);
}
```

#### Step 4: Repository saves to database
```java
// UserRepository.save() executes:
// INSERT INTO users (username, email, phone_number, role, is_active, is_verified, created_at, updated_at)
// VALUES ('John Doe', 'john@example.com', '9876543210', 'USER', true, false, NOW(), NOW())

// AccountsRepository.save() executes:
// INSERT INTO accounts (account_number, account_type, balance, currency, is_active, user_id, created_at, updated_at)
// VALUES ('1234567890', 'SAVINGS', 0.00, 'INR', true, <user_id>, NOW(), NOW())
```

#### Step 5: Response sent back to client
```json
HTTP/1.1 201 Created
Content-Type: application/json

{
  "statuscode": "201",
  "responseMsg": "Account created successfully"
}
```

---

### Scenario: Fetch Account Details

#### Step 1: Client sends HTTP Request
```bash
GET /api/accounts/fetch?phoneNumber=9876543210
```

#### Step 2: Controller receives request
```java
@GetMapping("/fetch")
public ResponseEntity<UserDto> fetchAccountDetails(@RequestParam String phoneNumber) {
    UserDto userDto = accountsService.fetchAccount(phoneNumber);
    return ResponseEntity.status(HttpStatus.OK).body(userDto);
}
```

#### Step 3: Service retrieves data
```java
@Override
public UserDto fetchAccount(String phoneNumber) {
    // Step 3a: Find user by phone number
    User user = userRepository.findByPhoneNumber(phoneNumber)
        .orElseThrow(() -> new ResourceNotFoundException("User", "phoneNumber", phoneNumber));
    
    // Step 3b: Find account for this user
    Account account = accountsRepository.findByUserId(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Account", "userId", user.getId().toString()));
    
    // Step 3c: Map entity to DTO
    UserDto userDto = UserMapper.mapToUserDto(user, new UserDto());
    userDto.setAccountNumber(account.getAccountNumber());
    userDto.setAccountType(account.getAccountType().name());
    
    return userDto;
}
```

#### Step 4: Repository queries database
```java
// UserRepository.findByPhoneNumber() executes:
// SELECT * FROM users WHERE phone_number = '9876543210'

// AccountsRepository.findByUserId() executes:
// SELECT * FROM accounts WHERE user_id = <user_id>
```

#### Step 5: Response sent back to client
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "userName": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "9876543210",
  "accountNumber": "1234567890",
  "accountType": "SAVINGS"
}
```

---

## Best Practices

### 1. **Separation of Concerns**
- Each layer has a specific responsibility
- Controller handles HTTP, Service handles business logic, Repository handles data access
- Easy to test and maintain

### 2. **Use Interfaces**
- Service interface allows multiple implementations
- Easy to mock for testing
- Loose coupling between layers

### 3. **DTOs for API**
- Never expose entities directly in API responses
- Allows schema changes without breaking API
- Hides sensitive information

### 4. **Validation**
- Validate at API boundary using @Valid
- Validate business rules in service layer
- Return meaningful error messages

### 5. **Exception Handling**
- Use custom exceptions for business logic errors
- Global exception handler for consistent error format
- Proper HTTP status codes

### 6. **Mapper Utility**
- Centralize entity-to-DTO conversion
- Reusable across multiple controllers
- Easy to maintain

### 7. **Constants**
- Centralize all constants
- Avoid magic strings in code
- Easy to update globally

### 8. **Lombok Annotations**
- Reduces boilerplate code
- @Getter, @Setter for getters/setters
- @AllArgsConstructor for constructor injection
- @Data for all of the above

### 9. **Dependency Injection**
- Use constructor injection with @AllArgsConstructor
- Immutable dependencies (final fields)
- Easy to test with mock objects

### 10. **Database Design**
- Proper indexes on frequently queried columns
- Foreign key constraints for referential integrity
- Appropriate data types and constraints

---

## Summary

This architecture provides:
- ✅ Clear separation of concerns
- ✅ Easy to test and maintain
- ✅ Scalable and extensible
- ✅ Consistent error handling
- ✅ Reusable components
- ✅ Industry best practices

Each layer has a specific responsibility, making the codebase clean, organized, and professional.