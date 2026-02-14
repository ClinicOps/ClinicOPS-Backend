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
        // extract userId from security context
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        if (userId == null) {
            return Optional.empty();
        }

        return Optional.of(new ObjectId(userId));
    }
}
