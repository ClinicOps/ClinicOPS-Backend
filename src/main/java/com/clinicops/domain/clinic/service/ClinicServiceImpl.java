package com.clinicops.domain.clinic.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinicops.domain.clinic.dto.CreateClinicRequest;
import com.clinicops.domain.clinic.model.Clinic;
import com.clinicops.domain.clinic.model.ClinicMember;
import com.clinicops.domain.clinic.model.ClinicRole;
import com.clinicops.domain.clinic.model.ClinicStatus;
import com.clinicops.domain.clinic.model.MembershipStatus;
import com.clinicops.domain.clinic.repository.ClinicMemberRepository;
import com.clinicops.domain.clinic.repository.ClinicRepository;
import com.clinicops.domain.organization.model.Organization;
import com.clinicops.domain.organization.model.OrganizationStatus;
import com.clinicops.domain.organization.repository.OrganizationRepository;
import com.clinicops.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClinicServiceImpl implements ClinicService {
	
	private OrganizationRepository organizationRepository;
	private ClinicRepository clinicRepository;
	private ClinicMemberRepository clinicMemberRepository;

	@Transactional
	public Clinic createInitialClinic(CreateClinicRequest request) {

		ObjectId userId = SecurityUtils.getCurrentUserId();
		
		List<ClinicMember> memberships =
			    clinicMemberRepository.findByUserIdAndDeletedFalse(userId);

		ObjectId organizationId;

		if (memberships.isEmpty()) {

		    // First clinic → create org
		    Organization org = new Organization();
		    org.setName(request.getOrganizationName());
		    org.setCode(generateSlug(request.getOrganizationName()));
		    org.setStatus(OrganizationStatus.ACTIVE);
		    org.setDeleted(false);

		    organizationRepository.save(org);

		    organizationId = org.getId();

		} else {

		    // Existing user → reuse org
		    organizationId = memberships.get(0).getOrganizationId();
		}

		// 2. Create Clinic
		Clinic clinic = new Clinic();
		clinic.setName(request.getClinicName());
		clinic.setCode(generateSlug(request.getClinicName()));
		clinic.setOrganizationId(organizationId);
		clinic.setTimezone(request.getTimezone());
		clinic.setStatus(ClinicStatus.ACTIVE);
		clinic.setDeleted(false);

		clinicRepository.save(clinic);

		// 3. Create Membership
		ClinicMember member = new ClinicMember();
		member.setUserId(userId);
		member.setOrganizationId(organizationId);
		member.setClinicId(clinic.getId());
		member.setRole(ClinicRole.CLINIC_ADMIN);
		member.setStatus(MembershipStatus.ACTIVE);
		member.setDeleted(false);

		clinicMemberRepository.save(member);

		return clinic;
	}
	
	private String generateSlug(String input) {

	    String base = input
	            .toLowerCase()
	            .replaceAll("[^a-z0-9\\s-]", "")
	            .trim()
	            .replaceAll("\\s+", "-");

	    String slug = base;
	    int counter = 1;

	    while (organizationRepository.existsByCodeAndDeletedFalse(slug)
	            || clinicRepository.existsByCodeAndDeletedFalse(slug)) {

	        slug = base + "-" + counter++;
	    }

	    return slug;
	}

}
