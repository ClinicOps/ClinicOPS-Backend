package com.clinicops.bootstrap;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IndexCreator {

    private final MongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void createIndexes() {

        // Doctor license unique
        mongoTemplate.indexOps("doctors")
                .createIndex(new Index()
                        .on("licenseNumber", Sort.Direction.ASC)
                        .unique());

        // ClinicDoctor unique (clinicId + doctorId)
        mongoTemplate.indexOps("clinic_doctors")
                .createIndex(new Index()
                        .on("clinicId", Sort.Direction.ASC)
                        .on("doctorId", Sort.Direction.ASC)
                        .unique());

        // ClinicDoctor specialization index
        mongoTemplate.indexOps("clinic_doctors")
                .createIndex(new Index()
                        .on("clinicId", Sort.Direction.ASC)
                        .on("specializations", Sort.Direction.ASC));

        // ClinicDoctor status index
        mongoTemplate.indexOps("clinic_doctors")
                .createIndex(new Index()
                        .on("clinicId", Sort.Direction.ASC)
                        .on("status", Sort.Direction.ASC));

        // ClinicDoctor archived filter index
        mongoTemplate.indexOps("clinic_doctors")
                .createIndex(new Index()
                        .on("clinicId", Sort.Direction.ASC)
                        .on("archived", Sort.Direction.ASC));
        
        mongoTemplate.indexOps("audit_logs")
		        .createIndex(new Index()
		                .on("clinicId", Sort.Direction.ASC)
		                .on("timestamp", Sort.Direction.DESC));

        mongoTemplate.indexOps("audit_logs")
		        .createIndex(new Index()
		                .on("userId", Sort.Direction.ASC));

        mongoTemplate.indexOps("audit_logs")
		        .createIndex(new Index()
		                .on("timestamp", Sort.Direction.DESC));
        
        mongoTemplate.indexOps("patients")
        .createIndex(new Index()
                .on("status", Sort.Direction.DESC));
    }
}