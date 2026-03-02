package com.clinicops.security;

import java.util.ArrayList;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MongoAuditorAware implements AuditorAware<ObjectId> {

    @Override
    public Optional<ObjectId> getCurrentAuditor() {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return Optional.empty();
        }
        
        AuthenticatedUser user = auth != null && auth.getPrincipal() instanceof AuthenticatedUser
        		? (AuthenticatedUser) auth.getPrincipal()
        				: null;
        
        ObjectId userObjId = user.getUserId();

        System.out.println("AUDITOR PRINCIPAL: " + auth.getName());


        return Optional.of(userObjId);
    }
}

