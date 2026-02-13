package com.clinicops.domain.access.repository;

import com.clinicops.domain.access.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface PermissionRepository extends MongoRepository<Permission, String> {}


