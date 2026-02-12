package com.clinicops.modules.emr.service;

import java.util.List;

import com.clinicops.modules.emr.dto.EmrCreateRequest;
import com.clinicops.modules.emr.dto.EmrResponse;
import com.clinicops.security.model.AuthUser;

public interface EmrService {

	EmrResponse createEmr(AuthUser user, EmrCreateRequest request);

	EmrResponse getLatestEmr(AuthUser user, String visitId);

	List<EmrResponse> getEmrHistory(AuthUser user, String visitId);
}
