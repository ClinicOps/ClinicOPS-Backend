package com.clinicops.domain.access.repository;

import com.clinicops.domain.access.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleAssignmentRepository
        extends MongoRepository<UserRoleAssignment, String> {

    List<UserRoleAssignment> findByUserIdAndClinicIdAndStatus(
            String userId, String clinicId, String status);
}
