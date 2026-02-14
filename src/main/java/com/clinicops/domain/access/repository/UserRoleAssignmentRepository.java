package com.clinicops.domain.access.repository;

import com.clinicops.domain.access.model.*;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleAssignmentRepository
        extends MongoRepository<UserRoleAssignment, ObjectId> {

    List<UserRoleAssignment> findByUserIdAndClinicIdAndStatus(
    		ObjectId userId, ObjectId clinicId, String status);
}
