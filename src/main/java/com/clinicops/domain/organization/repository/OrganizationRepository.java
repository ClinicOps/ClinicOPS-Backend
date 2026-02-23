package com.clinicops.domain.organization.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.domain.organization.model.Organization;

public interface OrganizationRepository extends MongoRepository<Organization, ObjectId> {

	boolean existsByCode(String code);

	boolean existsByCodeAndDeletedFalse(String slug);

}
