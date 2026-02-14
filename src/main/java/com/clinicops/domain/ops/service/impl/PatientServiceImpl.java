package com.clinicops.domain.ops.service.impl;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.clinicops.domain.ops.model.Gender;
import com.clinicops.domain.ops.model.Patient;
import com.clinicops.domain.ops.model.PatientContact;
import com.clinicops.domain.ops.model.PatientCounter;
import com.clinicops.domain.ops.model.PatientMedical;
import com.clinicops.domain.ops.model.PatientPersonal;
import com.clinicops.domain.ops.model.PatientStatus;
import com.clinicops.domain.ops.repository.PatientRepository;
import com.clinicops.domain.ops.service.PatientService;
import com.clinicops.web.ops.dto.CreatePatientRequest;
import com.clinicops.web.ops.dto.PatientResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public PatientResponse create(String clinicIdStr, CreatePatientRequest req) {

        ObjectId clinicId = new ObjectId(clinicIdStr);
        
        if (!ObjectId.isValid(clinicIdStr)) {
            throw new IllegalArgumentException("Invalid clinicId");
        }

        if (patientRepository.existsByClinicIdAndContactMobile(clinicId, req.getMobile())) {
            throw new IllegalStateException("Mobile already exists");
        }

        String patientCode = generatePatientCode(clinicId); 

        PatientPersonal personal = new PatientPersonal(
                req.getFirstName(),
                req.getLastName(),
                Gender.valueOf(req.getGender()),
                req.getDateOfBirth(),
                req.getBloodGroup(),
                req.getPhotoUrl()
        );

        PatientContact contact = new PatientContact(
                req.getMobile(),
                req.getEmail(),
                req.getAddress(),
                req.getCity(),
                req.getState(),
                req.getPincode()
        );

        PatientMedical medical = new PatientMedical(
                req.getAllergies(),
                req.getChronicConditions(),
                req.getNotes()
        );

        Patient patient = new Patient(
                clinicId,
                patientCode,
                personal,
                contact,
                medical
        );

        System.out.println("Creating patient for clinic: " + clinicId);
        try {
            Patient saved = patientRepository.save(patient);
            return toResponse(saved);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Page<PatientResponse> list(String clinicIdStr, Pageable pageable) {

        ObjectId clinicId = new ObjectId(clinicIdStr);

        Page<PatientResponse> res = patientRepository
                .findByClinicIdAndStatusNot(clinicId, PatientStatus.ARCHIVED, pageable)
                .map(this::toResponse);
        return res;
    }

    private PatientResponse toResponse(Patient p) {
        return PatientResponse.builder()
                .id(p.getId().toHexString())
                .patientCode(p.getPatientCode())
                .firstName(p.getPersonal().getFirstName())
                .lastName(p.getPersonal().getLastName())
                .mobile(p.getContact().getMobile())
                .email(p.getContact().getEmail())
                .status(p.getStatus().name())
                .build();
    }
    
    @Override
    public PatientResponse update(String clinicIdStr,
                                  String patientIdStr,
                                  CreatePatientRequest req) {

        if (!ObjectId.isValid(clinicIdStr) || !ObjectId.isValid(patientIdStr)) {
            throw new IllegalArgumentException("Invalid id");
        }

        ObjectId clinicId = new ObjectId(clinicIdStr);
        ObjectId patientId = new ObjectId(patientIdStr);

        Patient patient = patientRepository
                .findByClinicIdAndId(clinicId, patientId)
                .orElseThrow(() -> new IllegalStateException("Patient not found"));

        PatientContact contact = new PatientContact(
                req.getMobile(),
                req.getEmail(),
                req.getAddress(),
                req.getCity(),
                req.getState(),
                req.getPincode()
        );

        PatientMedical medical = new PatientMedical(
                req.getAllergies(),
                req.getChronicConditions(),
                req.getNotes()
        );

        patient.updateContact(contact);
        patient.updateMedical(medical);

        Patient saved = patientRepository.save(patient);

        return toResponse(saved);
    }

    @Override
    public void archive(String clinicIdStr, String patientIdStr) {

        ObjectId clinicId = new ObjectId(clinicIdStr);
        ObjectId patientId = new ObjectId(patientIdStr);

        Patient patient = patientRepository
                .findByClinicIdAndId(clinicId, patientId)
                .orElseThrow(() -> new IllegalStateException("Patient not found"));

        patient.archive();

        patientRepository.save(patient);
    }

    @Override
    public void activate(String clinicIdStr, String patientIdStr) {

        ObjectId clinicId = new ObjectId(clinicIdStr);
        ObjectId patientId = new ObjectId(patientIdStr);

        Patient patient = patientRepository
                .findByClinicIdAndId(clinicId, patientId)
                .orElseThrow(() -> new IllegalStateException("Patient not found"));

        patient.activate();

        patientRepository.save(patient);
    }
	
	private String generatePatientCode(ObjectId clinicId) {

        Query query = new Query(Criteria.where("_id").is(clinicId));

        Update update = new Update().inc("sequence", 1);

        FindAndModifyOptions options = new FindAndModifyOptions()
                .returnNew(true)
                .upsert(true);

        PatientCounter counter = mongoTemplate.findAndModify(
                query,
                update,
                options,
                PatientCounter.class
        );

        long seq = counter.getSequence();

        return String.format("PT-%06d", seq);
    }

}
