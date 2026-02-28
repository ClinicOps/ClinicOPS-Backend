package com.clinicops.domain.access.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private String userId;
    private String email;
    private String organizationId;
    private String clinicId;
    private String clinicName;
    private String clinicTimezone;
    private String role;
}
