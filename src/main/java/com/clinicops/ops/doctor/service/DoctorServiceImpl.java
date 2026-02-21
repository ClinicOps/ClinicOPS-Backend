package com.clinicops.ops.doctor.service;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.clinicops.common.api.PageResponse;
import com.clinicops.common.exception.BusinessException;
import com.clinicops.common.exception.NotFoundException;
import com.clinicops.common.exception.ValidationException;
import com.clinicops.ops.doctor.dto.ChangeDoctorStatusRequest;
import com.clinicops.ops.doctor.dto.CreateDoctorRequest;
import com.clinicops.ops.doctor.dto.DoctorResponse;
import com.clinicops.ops.doctor.dto.UpdateDoctorRequest;
import com.clinicops.ops.doctor.model.ClinicDoctor;
import com.clinicops.ops.doctor.model.Doctor;
import com.clinicops.ops.doctor.model.DoctorStatus;
import com.clinicops.ops.doctor.repository.ClinicDoctorRepository;
import com.clinicops.ops.doctor.repository.DoctorRepository;

import io.jsonwebtoken.lang.Objects;

public class DoctorServiceImpl implements DoctorService{
	
	@Autowired
	DoctorRepository doctorRepository;
	@Autowired
	ClinicDoctorRepository clinicDoctorRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Override
	public DoctorResponse createDoctor(ObjectId clinicId, CreateDoctorRequest request) {

	    Doctor doctor = doctorRepository
	            .findByLicenseNumber(request.getLicenseNumber())
	            .orElseGet(() -> {
	                Doctor newDoctor = new Doctor();
	                newDoctor.setLicenseNumber(request.getLicenseNumber());
	                newDoctor.setFirstName(request.getFirstName());
	                newDoctor.setLastName(request.getLastName());
	                newDoctor.setPhone(request.getPhone());
	                newDoctor.setEmail(request.getEmail());
	                newDoctor.setQualifications(request.getQualifications());
	                newDoctor.setProfileImageUrl(request.getProfileImageUrl());
	                return doctorRepository.save(newDoctor);
	            });

	    if (clinicDoctorRepository.existsByClinicIdAndDoctorId(clinicId, doctor.getId())) {
	        throw new BusinessException("Doctor already assigned to clinic");
	    }

	    ClinicDoctor clinicDoctor = new ClinicDoctor();
	    clinicDoctor.setClinicId(clinicId);
	    clinicDoctor.setDoctorId(doctor.getId());
	    clinicDoctor.setSpecializations(request.getSpecializations());
	    clinicDoctor.setConsultationFee(request.getConsultationFee());
	    clinicDoctor.setStatus(request.getStatus());
	    clinicDoctor.setAvailable(true);
	    clinicDoctor.setArchived(false);

	    validateVisitingConfiguration(request.getStatus(),
	            request.getVisitingFrom(),
	            request.getVisitingTo());

	    clinicDoctor.setVisitingFrom(request.getVisitingFrom());
	    clinicDoctor.setVisitingTo(request.getVisitingTo());

	    clinicDoctorRepository.save(clinicDoctor);

	    return mapToResponse(doctor, clinicDoctor);
	}
	
	private void validateVisitingConfiguration(
	        DoctorStatus status,
	        LocalDate from,
	        LocalDate to) {

	    if (status == DoctorStatus.VISITING) {
	        if (from == null || to == null) {
	            throw new ValidationException("Visiting period required");
	        }
	        if (to.isBefore(from)) {
	            throw new ValidationException("Invalid visiting range");
	        }
	    } else {
	        if (from != null || to != null) {
	            throw new ValidationException("Visiting dates allowed only for VISITING doctors");
	        }
	    }
	}
	
	private boolean computeEffectiveAvailability(ClinicDoctor cd) {

	    if (cd.getStatus() == DoctorStatus.VISITING) {
	        LocalDate today = LocalDate.now();
	        if (today.isBefore(cd.getVisitingFrom()) ||
	            today.isAfter(cd.getVisitingTo())) {
	            return false;
	        }
	    }

	    return Boolean.TRUE.equals(cd.getAvailable());
	}
	
	@Override
	public PageResponse<DoctorResponse> listDoctors(
	        ObjectId clinicId,
	        String search,
	        String specialization,
	        DoctorStatus status,
	        Boolean available,
	        int page,
	        int size) {

	    Criteria criteria = Criteria.where("clinicId").is(clinicId)
	            .and("archived").is(false);

	    if (status != null) {
	        criteria.and("status").is(status);
	    }

	    if (specialization != null) {
	        criteria.and("specializations").is(specialization);
	    }

	    Query query = new Query(criteria);
	    query.with(PageRequest.of(page, size));

	    List<ClinicDoctor> clinicDoctors =
	            mongoTemplate.find(query, ClinicDoctor.class);

	    long total = mongoTemplate.count(query.skip(-1).limit(-1), ClinicDoctor.class);

	    List<DoctorResponse> responses = clinicDoctors.stream()
	            .map(cd -> {
	                Doctor doctor = doctorRepository.findById(cd.getDoctorId())
	                        .orElseThrow(() -> new NotFoundException("Doctor missing"));

	                DoctorResponse response = mapToResponse(doctor, cd);

	                if (available != null &&
	                        response.getAvailable() != available) {
	                    return null;
	                }

	                if (search != null &&
	                        !doctor.getFirstName().toLowerCase().contains(search.toLowerCase()) &&
	                        !doctor.getLastName().toLowerCase().contains(search.toLowerCase())) {
	                    return null;
	                }

	                return response;
	            })
	            .filter(Objects::nonNull)
	            .toList();

	    return PageResponse.of(responses, page, size, total);
	}
	
	private DoctorResponse mapToResponse(Doctor doctor, ClinicDoctor cd) {

	    DoctorResponse response = new DoctorResponse();

	    response.setId(doctor.getId().toHexString());
	    response.setClinicDoctorId(cd.getId().toHexString());

	    response.setFirstName(doctor.getFirstName());
	    response.setLastName(doctor.getLastName());
	    response.setLicenseNumber(doctor.getLicenseNumber());
	    response.setProfileImageUrl(doctor.getProfileImageUrl());

	    response.setSpecializations(cd.getSpecializations());
	    response.setConsultationFee(cd.getConsultationFee());
	    response.setStatus(cd.getStatus());
	    response.setArchived(cd.getArchived());

	    response.setAvailable(computeEffectiveAvailability(cd));

	    return response;
	}

	@Override
	public DoctorResponse updateDoctor(ObjectId clinicId, ObjectId clinicDoctorId, UpdateDoctorRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changeStatus(ObjectId clinicId, ObjectId clinicDoctorId, ChangeDoctorStatusRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void archiveDoctor(ObjectId clinicId, ObjectId clinicDoctorId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DoctorResponse getDoctor(ObjectId clinicId, ObjectId clinicDoctorId) {
		// TODO Auto-generated method stub
		return null;
	}

}
