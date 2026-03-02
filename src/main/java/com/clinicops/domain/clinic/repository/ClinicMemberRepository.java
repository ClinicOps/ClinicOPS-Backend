package com.clinicops.domain.clinic.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.domain.clinic.model.ClinicMember;
import com.clinicops.domain.clinic.model.MembershipStatus;

public interface ClinicMemberRepository extends MongoRepository<ClinicMember, ObjectId>{

	List<ClinicMember> findByUserIdAndDeletedFalse(ObjectId userId);

	boolean existsByUserIdAndDeletedFalse(ObjectId userId);

	boolean existsByUserIdAndClinicIdAndStatusAndDeletedFalse(ObjectId userId, ObjectId clinicId,
			MembershipStatus active);

	List<ClinicMember> findByUserIdAndStatusAndDeletedFalse(ObjectId userId, MembershipStatus active);

}

