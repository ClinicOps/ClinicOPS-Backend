package com.clinicops.modules.auth.dto;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}
