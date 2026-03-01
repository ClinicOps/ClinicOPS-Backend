# ClinicOPS Backend - AI Coding Agent Instructions

## Project Overview
ClinicOPS Backend is a Spring Boot 4.0.2 clinic management platform built with Java 17, using MongoDB for persistence, RabbitMQ for async messaging, and Redis for caching. The system manages clinics, doctors, patients, appointments, and role-based access control.

## Architecture & Core Concepts

### Layered Domain Architecture
The codebase follows a layered structure with domain-driven design:
- **domain/**: Core business domains (clinic, user, organization, access control)
- **ops/**: Operational aggregates for appointment, doctor, patient, and availability management
- **config/**: Spring configuration (Security, MongoDB, RabbitMQ, Redis)
- **infra/**: Infrastructure patterns (messaging, persistence, redis)
- **security/**: Authentication/authorization (JWT, filters, permission evaluation)
- **common/**: Shared utilities (exception handling, API response models, audit)

### Multi-Tenancy Model
- Requests require `X-Clinic-Id` header (enforced by `ClinicContextFilter`)
- User context includes: userId, organizationId, clinicId, role (extracted from JWT)
- All queries filter by clinicId to ensure data isolation
- Exception: `/auth/**` and `/me` endpoints bypass clinic context requirement

### Permission Model (Domain-Driven)
Located in `domain/access/`:
- Users have Role assignments (per clinic)
- Roles contain Permissions with key format: `domain:resource:action`
- `PermissionEvaluator.isAllowed()` checks permissions before operations
- Owner role bypasses all permission checks
- Example: `clinic:doctor:create`, `appointment:appointment:reschedule`

### Data Model Patterns
- **ObjectId** used throughout (MongoDB native type)
- Status enums control lifecycle: `ACTIVE`, `ARCHIVED`, `VISITING` (for doctors)
- **Snapshot pattern**: Some entities store user data snapshots (e.g., Appointment stores patient name at creation time)
- **Separate read models**: ClinicDoctor vs Doctor (normalized vs denormalized data)

## Technology Stack

### Database
- **MongoDB** (mongodb://localhost:27017/clinicops)
- Spring Data MongoDB with MongoTemplate for complex queries
- Use `Query` and `Criteria` for custom MongoDB operations (see `DoctorServiceImpl`)
- **Auditing enabled**: MongoAuditorAware tracks createdBy, lastModifiedBy, timestamps

### Messaging
- **RabbitMQ** (amqp://localhost:5672)
- Topic exchange: `clinicops.domain.events`
- Event routing by class name: `AppointmentBookedEvent` → routing key `appointment.booked`
- `EventPublisher` interface with `RabbitEventPublisher` implementation
- Events inherit from `BaseEvent` and include eventId, occurredAt

### Security & Authentication
- JWT tokens with HS256 (configured in application.properties)
- Access tokens include: userId, orgId, clinicId, role, type="access"
- Refresh tokens: Stored in `RefreshToken` collection with tokenHash (SHA256) and expiry
- `AuthFilter` validates JWT and populates SecurityContext
- Session-less (STATELESS policy)
- Token refresh flow: Verify refresh token hash against stored hash before issuing new access token

### Authentication Flow (Refactored)

**Register Flow** (`POST /auth/register`):
1. Validate email uniqueness, password length (min 6), and clinic code uniqueness
2. Create Organization (auto-generates code if not provided)
3. Create Clinic under Organization with timezone
4. Create User with bcrypt-encoded password
5. Assign OWNER role via `UserRoleAssignment` (links user → role → permissions)
6. Create `ClinicMember` entry (backward compatibility with login)
7. Generate access token (includes orgId, clinicId, OWNER role)
8. Save refresh token hash with 7-day expiry
9. Return `AuthResponse` with UserDTO (includes clinic details and timezone)

**Login Flow** (`POST /auth/login`):
1. Find user by email, validate password with bcrypt
2. Fetch `ClinicMember` entries to locate clinic assignment
3. Fetch clinic details for name and timezone
4. Generate access token (orgId, clinicId from membership)
5. Save refresh token hash
6. Return `AuthResponse` with UserDTO containing clinic context
7. Client must send `X-Clinic-Id` header on subsequent requests

### Response Format
All endpoints return `ApiResponse<T>`:
```java
{
  "success": boolean,
  "data": T,
  "message": String // only on errors
}
```

## Common Development Patterns

### Service Layer
- **Dual interface pattern**: Interface (e.g., `DoctorService`) + Implementation (e.g., `DoctorServiceImpl`)
- Use constructor injection with `@RequiredArgsConstructor` (Lombok)
- Validate before persistence; throw `BusinessException` for domain violations
- Use `NotFoundException`, `ValidationException` for specific error cases
- `IdentityService`: Orchestrates register/login with transactional multi-step flows including role assignment and audit publishing

### API Response Models
- `ApiResponse<T>`: Standard envelope for all API responses (success, data, message)
- `AuthResponse`: Contains accessToken, refreshToken, and UserDTO with clinic context
- `UserDTO`: Includes userId, email, organizationId, clinicId, clinicName, clinicTimezone, role (returned after auth)
- `RegisterRequest`: email, password, clinicName, clinicCode, clinicTimezone, organizationName
- `LoginRequest`: email, password (returns AuthResponse with populated UserDTO)

### Repository Patterns
- Extend `MongoRepository<T, ObjectId>`
- Custom finder methods: `findByLicenseNumber()`, `findByClinicIdAndDoctorId()`
- Pagination: `Page<T> findByClinicIdAndArchivedFalse(ObjectId clinicId, Pageable pageable)`
- Always filter by clinicId for multi-tenant isolation

### Exception Handling
- `GlobalExceptionHandler` catches exceptions and returns ApiResponse
- `BusinessException` → HTTP 400 with custom message
- Unhandled exceptions → HTTP 500 generic message
- Custom: `NotFoundException`, `ValidationException` (extend BusinessException)

### Controller Patterns
- `@RestController` with `@RequestMapping` base path
- Clinic-scoped endpoints: `/api/clinics/{clinicId}/resource`
- Operations-scoped: `/ops/appointments`
- Always include explicit clinic context in path or validate via filters

## Key Service Behaviors

### Doctor Management (ops/doctor/)
- Doctors are global; ClinicDoctor links doctor to clinic with specialization/fee
- Status includes: PERMANENT, VISITING (with date range validation)
- Visiting availability computed based on dates; `computeEffectiveAvailability()` checks current date
- Archiving soft-deletes via archivedFalse flag
- MongoTemplate used for bulk updates (see bulk-archive)

### Patient Management (ops/patient/)
- Patients are clinic-scoped
- Status: ACTIVE, ARCHIVED
- Cannot create appointments for archived patients
- PatientCounter, PatientAudit separate collections for tracking

### Appointment Management (ops/appointment/)
- Stores snapshot of patient name at booking time (snapshot pattern)
- Slot validation against DoctorAvailability and exceptions
- DuplicateKeyException caught for double-booking prevention
- Reschedule logic (commented) validates new slot availability

### Doctor Availability (ops/availability/)
- `DoctorAvailability`: Recurring slots by day of week
- `DoctorAvailabilityException`: Specific date overrides
- Slot generation combines recurring + exception handling
- Multiple day/time combinations per doctor

## Build & Run Commands

### Maven
```bash
./mvnw clean install          # Build and test
./mvnw spring-boot:run        # Run development server (port 8080)
./mvnw test                   # Run unit tests
```

### External Services Required
- MongoDB: `mongodb://localhost:27017`
- RabbitMQ: `amqp://localhost:5672`
- Redis: `localhost:6379`

All configured in `src/main/resources/application.properties`

## Critical Files & Patterns

| File | Purpose |
|------|---------|
| `SecurityConfig.java` | JWT validation, CORS, filter chain ordering |
| `AuthFilter.java` | Extracts JWT and populates AuthenticatedUser |
| `ClinicContextFilter.java` | Enforces X-Clinic-Id header requirement |
| `JwtService.java` | Token generation (access/refresh) with HS256 signing |
| `AuthController.java` | Endpoints: `/auth/register`, `/auth/login` |
| `IdentityService.java` | Orchestrates auth flows: register (with role assignment), login, audit publishing |
| `RoleAndPermissionSeeder.java` | Bootstrap seeding of roles and permissions on app startup |
| `PermissionEvaluator.java` | Role-based access control check (domain:resource:action format) |
| `RabbitEventPublisher.java` | Event publishing with auto routing-key generation |
| `GlobalExceptionHandler.java` | Unified error response formatting |
| `DoctorServiceImpl.java` | Service with MongoTemplate custom queries example |
| `ApiResponse.java` | Standard response envelope |

## Testing Notes
- Spring Boot Test with Testcontainers support (test dependencies available)
- Use `@SpringBootTest` for integration tests
- MongoDB, RabbitMQ, Redis have dedicated test starters

## Common Gotchas
1. **Clinic context**: Missing `X-Clinic-Id` header causes 400 error on most endpoints
2. **Permission keys**: Format must be `domain:resource:action` exactly
3. **ObjectId conversion**: Validate with `ObjectId.isValid(str)` before creating
4. **DoctorService dual model**: Always update both Doctor and ClinicDoctor consistently
5. **Visiting doctor dates**: Validation enforces visiting period when status=VISITING
6. **Event routing**: Naming must follow `CamelCaseEvent` → `camel.case` pattern
7. **Filter ordering**: AuthFilter must run before ClinicContextFilter (configured in SecurityConfig)

## Migration & Audit
- MongoDB audit enabled: every entity tracks createdBy, createdDate, lastModifiedBy, lastModifiedDate
- Implement `MongoAuditorAware` to inject current user into audit context

## Role & Permission Model

### Bootstrap Seeding
`RoleAndPermissionSeeder` runs on app startup (`ApplicationReadyEvent`) and seeds:
- **Permissions**: Fine-grained domain:resource:action keys (e.g., `clinic:clinic:create`, `ops:appointment:reschedule`)
- **Roles**: OWNER, ADMIN, STAFF with assigned permissions
- **Idempotent**: Checks existing permissions before re-seeding

### Permission Structure
- Format: `{domain}:{resource}:{action}`
- Domains: `clinic`, `user`, `role`, `ops`, `appointment`, etc.
- Resources: `clinic`, `doctor`, `patient`, `appointment`, etc.
- Actions: `create`, `view`, `update`, `delete`, `reschedule`, etc.
- Example permissions:
  - `ops:doctor:create` - Create doctors in ops
  - `ops:appointment:reschedule` - Reschedule appointments
  - `clinic:clinic:update` - Update clinic info

### Role Assignment
- `UserRoleAssignment` links user → clinic → role (with status ACTIVE/INACTIVE)
- Users can have different roles per clinic
- OWNER role bypasses all permission checks

## Frontend-Backend Sync Analysis

### ✅ Synchronized Components

**Authentication Flow**
- Frontend login/register components match backend API endpoints (`/auth/register`, `/auth/login`)
- Frontend `AuthResponse` model matches backend structure (accessToken, refreshToken, user)
- Frontend `UserDTO` matches backend structure exactly (userId, email, organizationId, clinicId, clinicName, clinicTimezone, role)
- Frontend uses bcrypt password validation (matches backend)
- Frontend stores tokens in localStorage and adds `Authorization: Bearer {token}` header via `AuthInterceptor`

**Permission Model**
- Frontend `PermissionService` loads permissions via `/me/permissions` endpoint (backend support confirmed)
- Frontend permission checks use same format: `domain:resource:action`
- Frontend wildcard `"*"` for OWNER role matches backend bypass behavior

**Clinic Context Management**
- Frontend `ClinicContextService` stores clinicId from auth response
- Frontend components set clinic context after successful registration
- Frontend `MeService` initialized with userId and clinicId from auth response

### ⚠️ SYNC ISSUES STATUS

**1. X-Clinic-Id Header** ✅ FIXED
- **Status**: X-Clinic-Id header is being sent on clinic-scoped endpoints
- **Implementation**: Frontend `api.interceptor.ts` adds header
- **Verification**: `/ops/appointments` loads successfully after register

**2. ApiResponse Wrapper** ✅ FIXED
- **Status**: Response unwrapping implemented and working
- **Implementation**: `responseUnwrapperInterceptor` unwraps `ApiResponse<T>` to `T`
- **Verification**: Frontend receives clean data structures from all endpoints

**3. JWT Token Extraction** ✅ FIXED
- **Status**: `JwtService.extractClaims()` now properly extracts token claims
- **Problem Fixed**: Method was returning `null`, breaking token validation in AuthFilter
- **Impact**: `/me` and `/me/permissions` endpoints now work correctly
- **Verification**: Permissions load after register without 401 errors

**4. MeController User Context** ✅ FIXED
- **Status**: Both `/me` and `/me/permissions` now get user from SecurityContext
- **Problem Fixed**: Was trying to get user from request attributes which were null
- **Impact**: Permission loading works correctly with proper user/clinic context
- **Verification**: Permissions display correctly in frontend after login

**5. End-to-End Register Flow** ✅ COMPLETE
- **Status**: Full register flow working end-to-end
- **Verified Path**: Register → Token Storage → Clinic Context → Permission Load → Navigate to /ops/appointments ✅
- **All Steps Working**: 
  - Register creates org, clinic, user, role assignment
  - Tokens generated and stored
  - User context available via `/me`
  - Permissions load via `/me/permissions`
  - Clinic-scoped endpoints accessible with X-Clinic-Id header

### 📋 Endpoint Status Summary

| Endpoint | Backend | Frontend | Status |
|----------|---------|----------|--------|
| `POST /auth/register` | ✅ | ✅ | ✅ Fully Working |
| `POST /auth/login` | ✅ | ✅ | ✅ Fully Working |
| `GET /me` | ✅ | ✅ | ✅ Fully Working |
| `GET /me/permissions` | ✅ | ✅ | ✅ Fully Working |
| `X-Clinic-Id header` | ✅ Required | ✅ Sending | ✅ Working |
| `GET /ops/appointments` | ✅ | ✅ | ✅ Fully Working |
| `POST /ops/appointments` | ✅ | ✅ | ✅ Ready to Test |
| `ApiResponse wrapper` | ✅ | ✅ | ✅ Unwrapping Correctly |
| Token refresh | ✅ Backend Ready | ⏳ TODO | Pending Frontend Implementation |

### 🟢 Completed Actions

1. ✅ **X-Clinic-Id header implementation** - Header now sent on all clinic-scoped requests
2. ✅ **ApiResponse wrapper handling** - Frontend properly unwraps `{success, data, message}` structure
3. ✅ **JWT token extraction** - `JwtService.extractClaims()` fixed to return Claims instead of null
4. ✅ **MeController user context** - Both `/me` and `/me/permissions` get user from SecurityContext
5. ✅ **End-to-end register flow** - Complete flow from registration through permissions loading

### 🟡 Remaining Actions

1. **Token refresh implementation** - Backend ready, frontend needs HttpInterceptor for 401 handling
2. **Endpoint path standardization** - Mix of clinic-scoped (`/api/clinics/{clinicId}/`) and ops-scoped (`/ops/`) endpoints
