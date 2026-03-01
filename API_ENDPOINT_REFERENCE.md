# API Endpoint Reference - Standardized `/ops/` Pattern

**Last Updated**: March 1, 2026  
**Pattern**: All operational resources use `/ops/{resource}` with JWT-based clinic context  
**Status**: ✅ All 22 endpoints standardized

---

## 🔐 Authentication Endpoints

These endpoints do NOT require clinic context.

```http
POST /auth/register
POST /auth/login
```

---

## 👤 User Context Endpoints

Get authenticated user information and permissions. Clinic context extracted from JWT.

```http
GET /me                      # Returns: { userId, clinicId }
GET /me/permissions          # Returns: Permission[] or "*" (OWNER role)
```

---

## 👨‍⚕️ Doctor Endpoints

All doctor operations use clinic ID from JWT (no path variable).

### List Doctors
```http
GET /ops/doctors
```
**Query Parameters**:
- `search` (optional) - Search by name/license
- `specialization` (optional) - Filter by specialization
- `status` (optional) - PERMANENT, VISITING, ARCHIVED
- `available` (optional) - true/false
- `page` (default: 0) - Page number
- `size` (default: 10) - Page size

**Response**: `ApiResponse<PageResponse<DoctorResponse>>`

### Get Doctor
```http
GET /ops/doctors/{id}
```
**Response**: `ApiResponse<DoctorResponse>`

### Create Doctor
```http
POST /ops/doctors
```
**Request Body**: `CreateDoctorRequest`
```json
{
  "firstName": "string",
  "lastName": "string",
  "licenseNumber": "string",
  "specializations": ["string"],
  "consultationFee": number,
  "status": "PERMANENT",
  "visitingFromDate": "ISO date (if VISITING)",
  "visitingToDate": "ISO date (if VISITING)"
}
```
**Response**: `ApiResponse<DoctorResponse>`

### Update Doctor
```http
PUT /ops/doctors/{id}
```
**Request Body**: `UpdateDoctorRequest` (same as CreateDoctorRequest)  
**Response**: `ApiResponse<DoctorResponse>`

### Change Doctor Status
```http
PATCH /ops/doctors/{id}/status
```
**Request Body**: `ChangeDoctorStatusRequest`
```json
{
  "status": "PERMANENT" | "VISITING" | "ARCHIVED",
  "visitingFromDate": "ISO date (if changing to VISITING)",
  "visitingToDate": "ISO date (if changing to VISITING)"
}
```
**Response**: `ApiResponse<void>`

### Archive Doctor
```http
DELETE /ops/doctors/{id}
```
**Response**: `ApiResponse<void>`

### Bulk Archive Doctors
```http
POST /ops/doctors/bulk-archive
```
**Request Body**: `List<String>` (doctor IDs)  
**Response**: `ApiResponse<void>`

### Export Doctors (CSV)
```http
GET /ops/doctors/export
```
**Response**: CSV file with doctor data

### Get Doctor Availability Slots
```http
GET /ops/doctors/{doctorId}/slots
```
**Query Parameters**:
- `date` (required) - ISO date format

**Response**: `List<SlotDTO>`

### Get Doctor Calendar
```http
GET /ops/doctors/{doctorId}/calendar
```
**Query Parameters**:
- `from` (required) - ISO date format
- `to` (required) - ISO date format

**Response**: `List<DailySlotsDTO>`

---

## 🧑‍🤝‍🧑 Patient Endpoints

All patient operations use clinic ID from JWT.

### List Patients
```http
GET /ops/patients
```
**Query Parameters**:
- `page` (default: 0) - Page number
- `size` (default: 10) - Page size
- `query` (optional) - Search string
- `status` (optional) - ACTIVE, ARCHIVED, ALL

**Response**: `Page<PatientResponse>`

### Get Patient
```http
GET /ops/patients/{patientId}
```
**Response**: `PatientResponse`

### Create Patient
```http
POST /ops/patients
```
**Request Body**: `CreatePatientRequest`
```json
{
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "phoneNumber": "string",
  "dateOfBirth": "ISO date",
  "gender": "MALE" | "FEMALE" | "OTHER"
}
```
**Response**: `PatientResponse`

### Update Patient
```http
PUT /ops/patients/{patientId}
```
**Request Body**: `CreatePatientRequest`  
**Response**: `PatientResponse`

### Archive Patient
```http
PATCH /ops/patients/{patientId}/archive
```
**Response**: `void`

### Activate Patient
```http
PATCH /ops/patients/{patientId}/activate
```
**Response**: `void`

---

## 📅 Appointment Endpoints

All appointment operations use clinic ID from JWT.

### List Appointments
```http
GET /ops/appointments
```
**Response**: `AppointmentDto[]`
```json
[
  {
    "id": "string",
    "patientNameSnapshot": "string",
    "scheduledAt": "ISO datetime",
    "status": "SCHEDULED" | "COMPLETED" | "CANCELLED"
  }
]
```

### Create Appointment
```http
POST /ops/appointments
```
**Request Body**:
```json
{
  "patientId": "string",
  "scheduledAt": "ISO datetime"
}
```
**Response**: `void`

### Cancel Appointment
```http
DELETE /ops/appointments/{appointmentId}
```
**Response**: `void`

---

## 🏥 Clinic Endpoints

Special setup endpoints. Do NOT require clinic context in JWT (public setup).

```http
POST /api/clinics/setup                    # Create initial clinic setup
GET /api/clinics/my-membership             # Check if user has clinic membership
```

---

## 🔑 Headers Required

All `/ops/**` and `/me/**` endpoints require:

```
Authorization: Bearer {accessToken}
X-Clinic-Id: {clinicId}                   # Automatically added by frontend interceptor
```

**Exception**: `/auth/**` endpoints don't require any headers  
**Exception**: `/api/clinics/**` endpoints may have different requirements

---

## 📊 API Response Format

All endpoints return `ApiResponse<T>` wrapper:

### Success Response (2xx)
```json
{
  "success": true,
  "data": { /* actual response data */ }
}
```

### Error Response (4xx, 5xx)
```json
{
  "success": false,
  "message": "error description"
}
```

**Frontend Note**: `responseUnwrapperInterceptor` automatically unwraps success responses, so services receive just the `data` part.

---

## 🛡️ Permission Requirements

| Endpoint | Permission | Details |
|----------|-----------|---------|
| GET /ops/doctors | OPS_DOCTOR_VIEW | List and view doctor details |
| POST /ops/doctors | OPS_DOCTOR_CREATE | Create new doctors |
| PUT /ops/doctors/{id} | OPS_DOCTOR_UPDATE | Update doctor information |
| DELETE /ops/doctors/{id} | OPS_DOCTOR_DELETE | Archive doctors |
| PATCH /ops/doctors/{id}/status | OPS_DOCTOR_UPDATE | Change doctor status |
| POST /ops/doctors/bulk-archive | OPS_DOCTOR_ARCHIVE | Bulk archive doctors |
| GET /ops/patients | OPS_PATIENT_VIEW | List and view patients |
| POST /ops/patients | OPS_PATIENT_CREATE | Create new patients |
| PUT /ops/patients/{id} | OPS_PATIENT_UPDATE | Update patient information |
| PATCH /ops/patients/{id}/archive | OPS_PATIENT_DELETE | Archive patients |
| PATCH /ops/patients/{id}/activate | OPS_PATIENT_UPDATE | Activate patients |
| POST /ops/appointments | OPS_APPOINTMENT_CREATE | Create appointments |
| GET /ops/appointments | OPS_APPOINTMENT_VIEW | List appointments |
| DELETE /ops/appointments/{id} | OPS_APPOINTMENT_DELETE | Cancel appointments |

**OWNER Role**: Bypasses all permission checks

---

## 🔄 Clinic Context

**Automatic Extraction**:
- Backend endpoints extract clinic ID from JWT claims
- No need to pass clinic ID in URL path
- `SecurityUtils.getCurrentClinicId()` provides clinic context

**Multi-Tenant Isolation**:
- Each request validated against clinic ID in JWT
- `ClinicContextFilter` enforces isolation
- All queries filtered by clinic ID
- Data cannot be accessed across clinic boundaries

---

## 📱 Frontend API Service Examples

### Doctor Operations
```typescript
// In DoctorApi service
constructor(private api: ApiClient) {}

list(params: any = {}) {
  return this.api.get<any>('/ops/doctors', { params });
}

create(payload: CreateDoctorRequest) {
  return this.api.post('/ops/doctors', payload);
}
```

### Patient Operations
```typescript
// In PatientsFacade service
load() {
  this.api.get<PageResponse<Patient>>(
    `/ops/patients?page=${page}&size=${size}&query=${query}&status=${status}`
  ).subscribe(res => { /* handle response */ });
}
```

### Appointment Operations
```typescript
// In AppointmentApi service
list() {
  return this.api.get<AppointmentDto[]>('/ops/appointments');
}

create(payload: { patientId: string; scheduledAt: string }) {
  return this.api.post<void>('/ops/appointments', payload);
}
```

---

## 🚀 Example Workflows

### Create Doctor and Check Availability
```bash
# 1. Create doctor
POST /ops/doctors
{
  "firstName": "John",
  "lastName": "Doe",
  "licenseNumber": "LIC123",
  "specializations": ["Cardiology"],
  "consultationFee": 100,
  "status": "PERMANENT"
}

# 2. Check doctor's availability slots for a specific date
GET /ops/doctors/{doctorId}/slots?date=2026-03-15

# 3. Get doctor's calendar for a date range
GET /ops/doctors/{doctorId}/calendar?from=2026-03-01&to=2026-03-31
```

### Book an Appointment
```bash
# 1. List patients to find patient ID
GET /ops/patients?query=smith

# 2. Check doctor availability
GET /ops/doctors/{doctorId}/slots?date=2026-03-15

# 3. Create appointment
POST /ops/appointments
{
  "patientId": "patient-id-123",
  "scheduledAt": "2026-03-15T10:00:00Z"
}
```

---

## ✅ Migration Status

All endpoints have been refactored from mixed patterns (`/api/clinics/{id}/` + `/ops/`) to a **unified `/ops/` pattern**.

**Key Benefits**:
- ✅ Consistent across all operational resources
- ✅ Clinic ID removed from URL (shorter, cleaner)
- ✅ JWT-based clinic isolation (more secure)
- ✅ Frontend simplified (no clinic context management)

---

## 📖 Related Documentation

- `ENDPOINT_STANDARDIZATION_MIGRATION.md` - Detailed migration guide
- `PATH_NAMING_ANALYSIS.md` - Original analysis and recommendations
- `SYNC_VERIFICATION_REPORT.md` - Full verification and security assessment
- `.github/copilot-instructions.md` - Complete development guide

---

**Generated**: March 1, 2026  
**Status**: ✅ All endpoints standardized and verified  
**Next Step**: Comprehensive testing of all 22 endpoints
