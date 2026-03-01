# Register Flow - Pre-Test Checklist

## Status Summary

### ✅ Issue #1: X-Clinic-Id Header (PARTIALLY FIXED)
- [x] Header added to some requests
- [ ] Header added to ALL non-auth requests
- **Issue**: `api.interceptor.ts` does NOT add X-Clinic-Id header
- **Impact**: POST /ops/appointments and other clinic-scoped endpoints will fail

### ✅ Issue #2: ApiResponse Wrapper (FIXED)
- [x] `responseUnwrapperInterceptor` implemented correctly
- [x] Interceptor registered in `app.config.ts`
- [x] Correctly unwraps before AuthService receives it
- **Status**: Working as expected

## Register Flow Components

### Backend ✅
- [x] `AuthController.register()` - returns wrapped `ApiResponse<AuthResponse>`
- [x] `IdentityService.register()` - 8-step flow (creates org, clinic, user, role assignment, tokens)
- [x] All steps logged with `log.info()`
- [x] Returns complete `UserDTO` with clinic context
- **Status**: Ready for testing

### Frontend
- [x] `RegisterComponent` - form validation, auth service call
- [x] `AuthService.register()` - makes HTTP request
- [x] `responseUnwrapperInterceptor` - unwraps ApiResponse
- [x] Response handled in `RegisterComponent.next()`
- [x] Sets clinic context via `ClinicContextService`
- [x] Initializes `MeService` with user data
- [ ] **ISSUE**: `api.interceptor.ts` missing X-Clinic-Id header

## Expected Flow After Register

```
1. POST /auth/register
   ↓
2. Backend creates all entities (org, clinic, user, role, tokens)
   ↓
3. Returns ApiResponse<AuthResponse> with user.clinicId
   ↓
4. responseUnwrapperInterceptor unwraps to AuthResponse
   ↓
5. AuthService.tap() stores tokens in localStorage
   ↓
6. RegisterComponent.next():
   - Clinic context set: localStorage['clinicops_clinic'] = clinicId
   - MeService initialized with userId, clinicId
   - Navigate to /ops/appointments
   ↓
7. api.interceptor adds:
   - Authorization: Bearer {token}
   - X-Clinic-Id: {clinicId} ← MISSING!
   ↓
8. GET /ops/appointments fails with 400 (missing X-Clinic-Id)
```

## Data to Verify in Console

### After register success:
```javascript
// In browser console
localStorage.getItem('accessToken')        // Should have JWT
localStorage.getItem('clinicops_clinic')   // Should have clinicId
localStorage.getItem('refreshToken')       // Should have JWT
```

### In Network Tab:
```
Request: POST /auth/register
Response: Should show UserDTO structure (after unwrap)
  {
    "userId": "...",
    "email": "...",
    "organizationId": "...",
    "clinicId": "...",
    "clinicName": "...",
    "clinicTimezone": "...",
    "role": "OWNER"
  }
```

### When navigating to /ops/appointments:
```
Request Headers:
  Authorization: Bearer {token}   ✅
  X-Clinic-Id: {clinicId}        ❌ MISSING
```

## What to Look For in Errors

### Error: `undefined is not an object (evaluating 'response.user')`
- ✅ Means responseUnwrapperInterceptor is NOT working
- Response structure: `{success, data, message}` not unwrapped

### Error: `400 Bad Request` on /ops/appointments after register
- ✅ Means X-Clinic-Id header is missing
- responseUnwrapperInterceptor IS working (register succeeded)
- api.interceptor needs to add X-Clinic-Id header

### Error on /ops/appointments: Clinic context required
- ✅ Means backend ClinicContextFilter is working correctly
- Frontend needs to send X-Clinic-Id header
- app state management missing

## Next Step

Share console output from:
1. **Browser console** (errors, logs)
2. **Network tab** (request/response details)
3. **Backend logs** (register flow steps)
