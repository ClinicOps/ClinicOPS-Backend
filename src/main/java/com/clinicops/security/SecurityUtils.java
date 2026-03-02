package com.clinicops.security;

import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.clinicops.common.exception.BusinessException;

public final class SecurityUtils {

	public SecurityUtils() {
	}

	public static ObjectId getCurrentUserId() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser)) {
			throw new BusinessException("User not authenticated");
		}

		AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

		return user.getUserId();
	}

	public static ObjectId getCurrentClinicId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser)) {
			throw new BusinessException("User not authenticated");
		}

		AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
		ObjectId clinicId = user.getClinicId();
		return clinicId;
	}
}
