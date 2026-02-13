package com.clinicops.domain.access.repository;

import com.clinicops.domain.access.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRoleAssignmentRepository
        extends MongoRepository<UserRoleAssignment, String> {

    List<UserRoleAssignment> findByUserIdAndClinicIdAndStatus(
            String userId, String clinicId, String status);
}
