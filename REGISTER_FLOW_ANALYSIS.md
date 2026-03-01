# Register Flow Issue Analysis

## Problem Identified

The frontend `AuthService` expects an `AuthResponse` object directly:
```typescript
// frontend/auth.service.ts
register(request: RegisterRequest) {
  return this.http.post<AuthResponse>(`${this.API}/register`, request)
    .pipe(
      tap(res => {
        this.storeTokens(res.accessToken, res.refreshToken); // ❌ FAILS HERE
      })
    );
}
```

But the backend returns a wrapped response:
```java
// backend/AuthController.java
@PostMapping("/register")
public ApiResponse<AuthResponse> register(@RequestBody RegisterRequest request) {
  AuthResponse response = identityService.register(request);
  return ApiResponse.ok(response); // Returns: {success: true, data: {...}, message: null}
}
```

## Data Flow Issue

**Backend Response**:
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "user": { ... }
  },
  "message": null
}
```

**Frontend Expects**:
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "user": { ... }
}
```

## Current Error

When `AuthService.register()` receives the wrapped response, it tries to access:
- `res.accessToken` → undefined (should be `res.data.accessToken`)
- `res.refreshToken` → undefined (should be `res.data.refreshToken`)

This causes:
1. Tokens not stored in localStorage
2. No clinic context set
3. Navigation may fail or user loses session

## Root Cause

**Issue #2 (ApiResponse Wrapper)** was listed as "fixed" but needs verification:
- [ ] `ApiResponseInterceptor` implementation
- [ ] Proper registration in app config
- [ ] Verification that response is being unwrapped before AuthService receives it

## Register Component Flow

```
RegisterComponent.register()
  ↓
AuthService.register()
  ↓
HTTP POST /auth/register
  ↓
Backend: Returns ApiResponse<AuthResponse>
  ↓
Frontend: Should unwrap via ApiResponseInterceptor
  ↓
AuthService tap(): stores tokens (IF response unwrapped)
  ↓
RegisterComponent next: sets clinic context + initializes MeService
  ↓
Navigate to /ops/appointments
```

## What Needs to Happen

1. **Verify ApiResponseInterceptor** is implemented and registered
2. **Test response unwrapping** in browser DevTools network tab
3. **Check AuthService** receives unwrapped AuthResponse
4. **Verify localStorage** has tokens after register
5. **Verify ClinicContextService** has clinicId

## Files to Check

**Frontend**:
- `src/app/core/api/api-response.interceptor.ts` (does it exist?)
- `src/main.ts` or `app.config.ts` (is interceptor registered?)
- Network tab in browser (what's the actual response?)

**Backend**:
- Logs from register endpoint (all steps complete?)
- Network response (is ApiResponse wrapper being sent?)

## Console Output Needed

Please share:

### Backend Console
```
[Log output from registration attempt showing all steps]
```

### Frontend Console
```
[Browser console logs showing error details]
Network tab → Response from /auth/register call
```

### Browser Network Tab
**Request**: POST /auth/register
**Response**: Raw JSON response structure

---

## Next Steps

1. Share console outputs from both backend and frontend
2. We'll identify exactly where the flow breaks
3. Fix the specific issue (likely ApiResponseInterceptor missing/misconfigured)
4. Test the complete flow end-to-end
