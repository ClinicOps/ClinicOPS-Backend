package com.clinicops.modules.workspace.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.modules.workspace.model.Workspace;

public interface WorkspaceRepository extends MongoRepository<Workspace, String> {
}
