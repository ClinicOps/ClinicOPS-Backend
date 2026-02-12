package com.clinicops.modules.auth.service;

import com.clinicops.modules.auth.dto.LoginRequest;
import com.clinicops.modules.auth.dto.LoginResponse;
import com.clinicops.modules.auth.dto.RefreshRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse refresh(RefreshRequest request);
}
