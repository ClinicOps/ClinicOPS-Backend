package com.clinicops.modules.visit.service;

import java.util.List;

import com.clinicops.modules.visit.dto.VisitCreateRequest;
import com.clinicops.modules.visit.dto.VisitResponse;
import com.clinicops.security.model.AuthUser;

public interface VisitService {

	VisitResponse createVisit(AuthUser user, VisitCreateRequest request);

	List<VisitResponse> getVisitHistory(AuthUser user, String patientId);
}
