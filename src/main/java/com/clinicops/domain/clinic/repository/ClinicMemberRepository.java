package com.clinicops.domain.clinic.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.domain.clinic.model.ClinicMember;

public interface ClinicMemberRepository extends MongoRepository<ClinicMember, ObjectId>{

	List<ClinicMember> findByUserIdAndDeletedFalse(ObjectId userId);

	boolean existsByUserIdAndDeletedFalse(ObjectId userId);

}

