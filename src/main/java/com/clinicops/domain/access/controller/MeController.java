package com.clinicops.domain.access.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.domain.access.model.Role;
import com.clinicops.domain.access.repository.PermissionRepository;
import com.clinicops.domain.access.repository.RoleRepository;
import com.clinicops.domain.access.repository.UserRoleAssignmentRepository;
import com.clinicops.security.AuthenticatedUser;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class MeController {
	
	@Autowired 
	UserRoleAssignmentRepository assignmentRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	PermissionRepository permissionRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Value("${spring.data.mongodb.uri:NOT_SET}")
    private String db;

	    @PostConstruct
	    public void logDb() {
	        System.out.println("Mongo DB in use = " + mongoTemplate.getDb().getName());
	        System.out.println("Configured Mongo DB = " + db);
	        System.out.println("MongoTemplate bean class = "
	                + mongoTemplate.getClass().getName());
	    }
	

    @GetMapping("/me")
    public Map<String, Object> me(HttpServletRequest request) {
        // Get user from SecurityContext (set by AuthFilter)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AuthenticatedUser user = auth != null && auth.getPrincipal() instanceof AuthenticatedUser
                ? (AuthenticatedUser) auth.getPrincipal()
                : null;
        
        // Get clinic from request attribute (set by ClinicContextFilter)
        String clinicId = (String) request.getAttribute("CLINIC_ID");
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user != null ? user.getUserId() : null);
        response.put("clinicId", clinicId);

        return response;
    }
    
    @GetMapping("/me/permissions")
    public List<String> myPermissions(HttpServletRequest request) {
        // Get user from SecurityContext (set by AuthFilter)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AuthenticatedUser user = auth != null && auth.getPrincipal() instanceof AuthenticatedUser
                ? (AuthenticatedUser) auth.getPrincipal()
                : null;
        
        if (user == null) {
            return new ArrayList<>();
        }
        
        
        ObjectId userObjId = user.getUserId();
        ObjectId clinicObjId = user.getClinicId();
        
        System.out.println("RBAC check userId=" + userObjId
        + " clinicId=" + clinicObjId);

        // Fetch ACTIVE role assignments
        var assignments =
                assignmentRepository.findByUserIdAndClinicIdAndStatus(
                		userObjId,
                        clinicObjId,
                        "ACTIVE"
                );

        Set<String> permissionKeys = new HashSet<>();

        for (var assignment : assignments) {
            Role role = roleRepository
                    .findById(assignment.getRoleId())
                    .orElse(null);

            if (role == null) continue;

            if (role.isOwner()) {
                return List.of("*"); // OWNER shortcut (frontend treats as all)
            }

            permissionRepository
                    .findAllById(role.getPermissionIds())
                    .forEach(p -> permissionKeys.add(p.key()));
        }

        return new ArrayList<>(permissionKeys);
    }
}
