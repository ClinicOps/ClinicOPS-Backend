package com.clinicops.modules.department.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.modules.department.model.Department;

public interface DepartmentRepository
extends MongoRepository<Department, String> {

List<Department> findByWorkspaceId(String workspaceId);
}