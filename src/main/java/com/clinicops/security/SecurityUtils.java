package com.clinicops.security;

import org.bson.types.ObjectId;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.clinicops.common.exception.BusinessException;

import jakarta.servlet.http.HttpServletRequest;

public final class SecurityUtils {

    public SecurityUtils() {}

    public static ObjectId getCurrentClinicId() {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes)
                        RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new BusinessException("No request context available");
        }

        HttpServletRequest request = attributes.getRequest();

        Object clinicIdAttr = request.getAttribute("CLINIC_ID");

        if (clinicIdAttr == null) {
            throw new BusinessException("Clinic context missing");
        }

        if (!(clinicIdAttr instanceof ObjectId)) {
            throw new BusinessException("Invalid clinic context");
        }

        return (ObjectId) clinicIdAttr;
    }
}
