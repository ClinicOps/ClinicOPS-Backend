package com.clinicops.ops.patient.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.clinicops.infra.messaging.EventPublisher;
import com.clinicops.ops.patient.counter.PatientCounter;
import com.clinicops.ops.patient.dto.CreatePatientRequest;
import com.clinicops.ops.patient.dto.PatientResponse;
import com.clinicops.ops.patient.model.Gender;
import com.clinicops.ops.patient.model.Patient;
import com.clinicops.ops.patient.model.PatientAudit;
import com.clinicops.ops.patient.model.PatientContact;
import com.clinicops.ops.patient.model.PatientMedical;
import com.clinicops.ops.patient.model.PatientPersonal;
import com.clinicops.ops.patient.repository.PatientAuditRepository;
import com.clinicops.ops.patient.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final MongoTemplate mongoTemplate;
    private final PatientAuditRepository patientAuditRepository;
    private final EventPublisher eventPublisher;
    
    @Override
    public Page<PatientResponse> list(String clinicIdStr,
            int page,
            int size,
            String query,
            String status) {

    	 ObjectId clinicId = new ObjectId(clinicIdStr);

    	    Query mongoQuery = new Query();
    	    mongoQuery.addCriteria(Criteria.where("clinicId").is(clinicId));

    	    if (status != null && !status.equalsIgnoreCase("ALL")) {
    	        mongoQuery.addCriteria(Criteria.where("status").is(status));
    	    }

    	    if (query != null && !query.isBlank()) {
    	        mongoQuery.addCriteria(new Criteria().orOperator(
    	                Criteria.where("personal.firstName").regex(query, "i"),
    	                Criteria.where("personal.lastName").regex(query, "i"),
    	                Criteria.where("contact.mobile").regex(query, "i")
    	        ));
    	    }

    	    long total = mongoTemplate.count(mongoQuery, Patient.class);

    	    mongoQuery.with(PageRequest.of(page, size));

    	    List<Patient> patients =
    	            mongoTemplate.find(mongoQuery, Patient.class);

    	    List<PatientResponse> responses =
    	            patients.stream().map(this::toResponse).toList();

    	    return new PageImpl<>(responses, PageRequest.of(page, size), total);
    	}

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

            Patient saved = patientRepository.save(patient);
            audit(saved, "CREATE", "Patient created");
            // Need to fix rabbitmq error
//            eventPublisher.publish(
//            	    new PatientCreatedEvent(
//            	        saved.getId().toHexString(),
//            	        saved.getClinicId().toHexString(),
//            	        saved.getPatientCode()
//            	    )
//            	);
            
            return toResponse(saved);

    }
    
    @Override
    public PatientResponse getById(String clinicIdStr, String patientIdStr) {

        if (!ObjectId.isValid(clinicIdStr) || !ObjectId.isValid(patientIdStr)) {
            throw new IllegalArgumentException("Invalid id");
        }

        ObjectId clinicId = new ObjectId(clinicIdStr);
        ObjectId patientId = new ObjectId(patientIdStr);

        Patient patient = patientRepository
                .findByClinicIdAndId(clinicId, patientId)
                .orElseThrow(() -> new IllegalStateException("Patient not found"));

        return toResponse(patient);
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
        audit(saved, "UPDATE", "Updated contact and medical");
//        eventPublisher.publish(
//        	    new PatientUpdatedEvent(
//        	        saved.getId().toHexString(),
//        	        saved.getClinicId().toHexString()
//        	    )
//        	);

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
        audit(patient, "ARCHIVE", "Patient archived");
//        eventPublisher.publish(
//        	    new PatientArchivedEvent(
//        	        patient.getId().toHexString(),
//        	        patient.getClinicId().toHexString()
//        	    )
//        	);
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
        audit(patient, "ACTIVATE", "Patient activated");
//        eventPublisher.publish(
//        	    new PatientActivatedEvent(
//        	        patient.getId().toHexString(),
//        	        patient.getClinicId().toHexString()
//        	    )
//        	);
    }
    
    private PatientResponse toResponse(Patient p) {
        return PatientResponse.builder()
                .id(p.getId().toHexString())
                .patientCode(p.getPatientCode())
                .firstName(p.getPersonal().getFirstName())
                .lastName(p.getPersonal().getLastName())
                .mobile(p.getContact().getMobile())
                .email(p.getContact().getEmail())
                .gender(p.getPersonal().getGender().toString())
                .status(p.getStatus().name())
                .build();
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
	
	private void audit(Patient patient, String action, String summary) {

	    var auth = org.springframework.security.core.context.SecurityContextHolder
	            .getContext()
	            .getAuthentication();

	    ObjectId performedBy = null;

	    if (auth != null && ObjectId.isValid(auth.getName())) {
	        performedBy = new ObjectId(auth.getName());
	    }

	    PatientAudit audit = new PatientAudit(
	            patient.getId(),
	            patient.getClinicId(),
	            action,
	            performedBy,
	            summary
	    );

	    patientAuditRepository.save(audit);
	}


}
