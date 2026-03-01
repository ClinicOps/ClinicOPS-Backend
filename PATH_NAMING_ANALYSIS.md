# Backend API Path Naming Analysis & Recommendations

**Generated**: March 1, 2026

---

## 📊 Current Path Analysis

### Existing Paths by Pattern

#### Pattern 1: `/api/clinics/{clinicId}/resource` (Clinic-Scoped)
- `/api/clinics/{clinicId}/doctors` - DoctorController
- `/api/clinics/{clinicId}/patients` - PatientController
- `/api/clinics` (setup operations) - ClinicController

**Characteristics**:
- Clinic ID embedded in URL path
- Uses path variables for clinic context
- REST-style sub-resource paths

#### Pattern 2: `/ops/resource` (Operations)
- `/ops/appointments` - AppointmentController

**Characteristics**:
- Clinic ID extracted from JWT claims
- No path variables for clinic context
- Shorter, cleaner URLs

#### Pattern 3: `/api/doctors` (Availability - Detached)
- `/api/doctors` - DoctorSlotController

**Characteristics**:
- Neither pattern
- Should extract clinicId from JWT
- Currently has commented-out endpoints

#### Pattern 4: `/auth` (Authentication)
- `/auth/register` - AuthController
- `/auth/login` - AuthController

**Characteristics**:
- No clinic context required
- Standard authentication endpoints

#### Pattern 5: Root/No Path (User Context)
- `/me` - MeController
- `/me/permissions` - MeController (implicit)

**Characteristics**:
- No path prefix
- Gets context from JWT
- Authenticated user endpoints

---

## 📋 Complete Endpoint Inventory

### Authentication Endpoints (No Clinic Context)
```
POST   /auth/register
POST   /auth/login
GET    /me
GET    /me/permissions
```
✅ **CONSISTENT** - All use `/auth` or root, no clinic context needed

---

### Clinic Setup Endpoints
```
POST   /api/clinics/setup
GET    /api/clinics/my-membership
```
✅ **OK** - Setup operations, not clinic-scoped

---

### Doctor Endpoints (Current: `/api/clinics/{clinicId}/doctors`)
```
POST   /api/clinics/{clinicId}/doctors
PUT    /api/clinics/{clinicId}/doctors/{id}
DELETE /api/clinics/{clinicId}/doctors/{id}
GET    /api/clinics/{clinicId}/doctors/{id}
GET    /api/clinics/{clinicId}/doctors
PATCH  /api/clinics/{clinicId}/doctors/{id}/status
POST   /api/clinics/{clinicId}/doctors/bulk-archive
GET    /api/clinics/{clinicId}/doctors/export
```
🔴 **INCONSISTENT** - Uses path variables when JWT has clinic context

---

### Patient Endpoints (Current: `/api/clinics/{clinicId}/patients`)
```
POST   /api/clinics/{clinicId}/patients
PUT    /api/clinics/{clinicId}/patients/{patientId}
GET    /api/clinics/{clinicId}/patients/{patientId}
GET    /api/clinics/{clinicId}/patients
PATCH  /api/clinics/{clinicId}/patients/{patientId}/archive
PATCH  /api/clinics/{clinicId}/patients/{patientId}/activate
```
🔴 **INCONSISTENT** - Uses path variables when JWT has clinic context

---

### Appointment Endpoints (Current: `/ops/appointments`)
```
POST   /ops/appointments
GET    /ops/appointments
DELETE /ops/appointments/{id}
```
✅ **CONSISTENT** - Uses JWT for clinic context

---

### Doctor Availability Endpoints (Current: `/api/doctors`)
```
GET    /api/doctors/{doctorId}/slots (commented)
GET    /api/doctors/{doctorId}/calendar (commented)
```
🟡 **UNCLEAR** - Incomplete, should follow standard pattern

---

## 🎯 Recommended Standardization

### Option A: Standardize to `/ops/` Pattern (Recommended)

**Advantages**:
- ✅ Removes clinic ID from URL (cleaner URLs)
- ✅ Matches `/ops/appointments` pattern already in use
- ✅ JWT-based clinic isolation (security best practice)
- ✅ Reduces frontend routing complexity
- ✅ More RESTful (clinic is implicit context)
- ✅ Fewer path variables to manage
- ✅ Easier to scale (no URL coupling)

**Implementation**:
```
# Doctor Endpoints (Refactored)
POST   /ops/doctors
PUT    /ops/doctors/{id}
DELETE /ops/doctors/{id}
GET    /ops/doctors/{id}
GET    /ops/doctors
PATCH  /ops/doctors/{id}/status
POST   /ops/doctors/bulk-archive
GET    /ops/doctors/export

# Patient Endpoints (Refactored)
POST   /ops/patients
PUT    /ops/patients/{patientId}
GET    /ops/patients/{patientId}
GET    /ops/patients
PATCH  /ops/patients/{patientId}/archive
PATCH  /ops/patients/{patientId}/activate

# Availability Endpoints (Complete)
GET    /ops/doctors/{doctorId}/slots
GET    /ops/doctors/{doctorId}/calendar
```

---

### Option B: Keep `/api/clinics/{clinicId}/` Pattern (Not Recommended)

**Advantages**:
- Consistent URL structure across resources
- Explicit clinic context in URL

**Disadvantages**:
- ❌ Longer URLs with duplicate clinic ID info
- ❌ Violates DRY principle (clinic in JWT + URL)
- ❌ More complex frontend routing
- ❌ Frontend must manage both JWT and URL clinic IDs
- ❌ Higher risk of clinic ID mismatch bugs
- ❌ Less RESTful (mixing patterns)

---

## 📋 Path Naming Convention Rules (If Standardized to `/ops/`)

### Rule 1: Resource Grouping
- Group related resources under domain: `/ops/{resource}`
- Resources: `appointments`, `doctors`, `patients`, `availability` (or use nested paths)

### Rule 2: Sub-resources
- Use nested paths for sub-resources: `/ops/{resource}/{id}/{sub-resource}`
- Example: `/ops/doctors/{doctorId}/slots`

### Rule 3: Bulk Operations
- Use `/bulk-{action}` suffix for bulk operations
- Example: `/ops/doctors/bulk-archive`

### Rule 4: Export Operations
- Use `/export` endpoint for data export
- Example: `/ops/doctors/export`

### Rule 5: Status Changes
- Use `/{id}/{resource}` or `/{id}/status` for status changes
- Example: `/ops/doctors/{id}/status`

### Rule 6: State Transitions
- Use verb-based paths for state changes: `/{id}/{action}`
- Examples: `/{id}/activate`, `/{id}/archive`

---

## 🔄 Migration Path

### Phase 1: Create New `/ops/` Endpoints (No Breaking Changes)
1. Create new endpoints under `/ops/doctors`, `/ops/patients`
2. Keep old endpoints for backward compatibility (at least 1 version)
3. Update documentation to recommend new endpoints

### Phase 2: Update Frontend
1. Update all API calls to use `/ops/` endpoints
2. Remove `clinicId` from URL paths
3. Rely on JWT for clinic context

### Phase 3: Deprecate Old Endpoints
1. Mark old endpoints as deprecated in code
2. Add deprecation warnings in API responses
3. Document migration path for API consumers

### Phase 4: Remove Old Endpoints
1. Remove `/api/clinics/{clinicId}/` endpoints
2. Clean up routing configurations
3. Update documentation

---

## 📊 Before/After Comparison

### Before (Current - Inconsistent)
```
POST   /auth/register                          ← Authentication
POST   /auth/login                              ← Authentication
GET    /me                                     ← User Context
GET    /me/permissions                         ← User Context
POST   /api/clinics/{clinicId}/doctors        ← Clinic-scoped
PUT    /api/clinics/{clinicId}/doctors/{id}   ← Clinic-scoped
GET    /api/clinics/{clinicId}/doctors        ← Clinic-scoped
POST   /api/clinics/{clinicId}/patients       ← Clinic-scoped
PUT    /api/clinics/{clinicId}/patients/{id}  ← Clinic-scoped
GET    /api/clinics/{clinicId}/patients       ← Clinic-scoped
POST   /ops/appointments                       ← Operations (JWT-based)
GET    /ops/appointments                       ← Operations (JWT-based)
```
🔴 **Problem**: Two different patterns for same type of resources

### After (Recommended - Consistent)
```
POST   /auth/register                          ← Authentication
POST   /auth/login                              ← Authentication
GET    /me                                     ← User Context
GET    /me/permissions                         ← User Context
POST   /ops/doctors                            ← Operations (JWT-based)
PUT    /ops/doctors/{id}                       ← Operations (JWT-based)
GET    /ops/doctors/{id}                       ← Operations (JWT-based)
GET    /ops/doctors                            ← Operations (JWT-based)
POST   /ops/patients                           ← Operations (JWT-based)
PUT    /ops/patients/{id}                      ← Operations (JWT-based)
GET    /ops/patients/{id}                      ← Operations (JWT-based)
GET    /ops/patients                           ← Operations (JWT-based)
POST   /ops/appointments                       ← Operations (JWT-based)
GET    /ops/appointments                       ← Operations (JWT-based)
```
✅ **Solution**: Consistent `/ops/` pattern for all operations

---

## 🛠️ Implementation Checklist

### Backend Changes
- [ ] Create new DoctorController with `/ops/doctors` paths
- [ ] Create new PatientController with `/ops/patients` paths
- [ ] Uncomment and fix DoctorSlotController endpoints under `/ops/`
- [ ] Update controller paths in all RequestMapping annotations
- [ ] Verify clinic context extraction from JWT works for all endpoints
- [ ] Update tests to use new paths
- [ ] Keep old endpoints for 1 release cycle (deprecated)

### Frontend Changes
- [ ] Update DoctorService API calls to `/ops/doctors`
- [ ] Update PatientService API calls to `/ops/patients`
- [ ] Remove `clinicId` from URL paths in API services
- [ ] Update routing (if any URL-based routing exists)
- [ ] Test all operations with new paths
- [ ] Remove old path references

### Documentation Changes
- [ ] Update API documentation with new paths
- [ ] Add migration guide for API consumers
- [ ] Document deprecation timeline
- [ ] Add examples of new endpoints

---

## 📑 Summary Table

| Pattern | Current Usage | Recommendation | Status |
|---------|---------------|-----------------|--------|
| `/auth/**` | Authentication | Keep as-is | ✅ Good |
| `/me` | User context | Keep as-is | ✅ Good |
| `/api/clinics/{id}/**` | Doctors, Patients | Migrate to `/ops/` | 🔄 Change |
| `/ops/**` | Appointments | Extend to all ops | ✅ Good |
| `/api/doctors` | Slots (disabled) | Move to `/ops/doctors` | 🔄 Change |

---

## 🎓 Key Benefits of Standardization

1. **Consistency**: Single pattern for all operational endpoints
2. **Simplicity**: Shorter URLs, fewer path variables
3. **Security**: Clinic isolation via JWT (harder to exploit)
4. **Scalability**: No URL coupling to organizational structure
5. **Maintainability**: Easier to understand and modify
6. **Frontend**: Simpler routing, less state management
7. **API Design**: Follows REST and Spring Boot best practices

---

## 🚀 Recommended Implementation Priority

**Priority 1 (High)**: Standardize to `/ops/` pattern
- Impacts: Doctor and Patient endpoints (13 total)
- Effort: 6-8 hours
- Benefits: Immediate consistency

**Priority 2 (Medium)**: Uncomment and fix availability endpoints
- Impacts: DoctorSlotController
- Effort: 2-4 hours
- Benefits: Feature completion

**Priority 3 (Low)**: Keep deprecated endpoints for backward compatibility
- Impacts: Old API consumers
- Effort: 1-2 hours
- Benefits: Smooth migration

---

## 📌 Final Recommendation

**Standardize all clinic-scoped operational endpoints to the `/ops/` pattern.**

This provides:
- ✅ A single, consistent API structure
- ✅ Cleaner, shorter URLs
- ✅ Better security (JWT-based isolation)
- ✅ Improved developer experience
- ✅ Future scalability
- ✅ Alignment with REST principles

**Implementation timeline**: 1-2 weeks for complete migration
