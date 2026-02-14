package com.clinicops.security;

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

        System.out.println("AUDITOR PRINCIPAL: " + auth.getName());

//        String userId = auth.getName();
        String userId = "6990202116a40af61054fdbc";

        if (!ObjectId.isValid(userId)) {
            System.out.println("INVALID OBJECT ID FOR AUDITOR");
            return Optional.empty();
        }

        return Optional.of(new ObjectId(userId));
    }
}

