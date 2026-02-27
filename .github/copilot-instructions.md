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
- Refresh tokens separate (type="refresh")
- `AuthFilter` validates JWT and populates SecurityContext
- Session-less (STATELESS policy)

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
| `PermissionEvaluator.java` | Role-based access control check |
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
