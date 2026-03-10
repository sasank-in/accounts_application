# Accounts Service - Architecture Guide

## Table of Contents
1. Project Overview
2. Package Structure
3. Data Flow
4. Component Details
5. API Behavior
6. Design Notes

---

## Project Overview
This is a Spring Boot service focused on user creation. It follows a layered architecture:

Request -> Controller -> Service -> Repository -> Database
Response <- Controller <- Service <- Repository

---

## Package Structure
Location: `src/main/java/com/eazybytes/accounts/`

- `constants/`
  - `UserConstants.java` - Centralized status/message constants (currently not used in controller responses).
- `controller/`
  - `UserController.java` - REST endpoint for user creation.
- `dto/`
  - `UserRequestDto.java` - Incoming payload (name, email).
  - `UserResponseDto.java` - Outgoing payload (id, name, email).
- `entity/`
  - `User.java` - JPA entity for users.
- `repository/`
  - `UserRepository.java` - Spring Data JPA repository for `User` with `findByEmail`.
- `service/`
  - `UserService.java` - Service contract.
  - `impl/UserServiceImpl.java` - Service implementation.

---

## Data Flow
Create user:
1. Client POSTs to `/api/create` with `UserRequestDto`.
2. `UserController` delegates to `UserService.createUser`.
3. `UserServiceImpl` checks for existing email and throws 409 if found.
4. A new `User` entity is created and saved via `UserRepository`.
5. The saved entity is mapped to `UserResponseDto` and returned.

---

## Component Details

### User Entity (`entity/User.java`)
Fields:
- `id` (UUID, generated)
- `name`
- `email`
- `phoneNumber` (present on entity, not populated by current request DTO)
- `createdAt`, `updatedAt` (managed in entity lifecycle callbacks)

### Service Contract (`service/UserService.java`)
- `createUser(UserRequestDto dto)`
- `getUserById(UUID id)` (not exposed by any controller yet)

### Service Implementation (`service/impl/UserServiceImpl.java`)
- Validates uniqueness by email using `UserRepository.findByEmail`.
- Throws `ResponseStatusException(HttpStatus.CONFLICT, ...)` on duplicates.
- Maps entity to response DTO manually.

### Controller (`controller/UserController.java`)
- Exposes `POST /api/create`.
- Returns HTTP 201 with `UserResponseDto`.

---

## API Behavior

### POST /api/create
Request body:
```json
{
  "name": "Jane Doe",
  "email": "jane@example.com"
}
```

Success response (201):
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "name": "Jane Doe",
  "email": "jane@example.com"
}
```

Error response:
- 409 Conflict if email already exists.

---

## Design Notes
- The service currently supports only user creation; read operations are not exposed at the API layer yet.
- `phoneNumber` exists on the entity but is not set by current DTOs. Add it to `UserRequestDto` if needed.
- `UserConstants` is available for standard messages if you want consistent API responses.

---

## Summary
This service is a minimal, layered Spring Boot application focused on user creation with email uniqueness enforced at the service layer.
