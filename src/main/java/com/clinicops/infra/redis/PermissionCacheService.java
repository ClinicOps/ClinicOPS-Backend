package com.clinicops.infra.redis;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String PERMISSION_CACHE_KEY_PREFIX = "permissions:";
    private static final long CACHE_TTL_MINUTES = 15;

    /**
     * Get cached permissions for a user in a clinic
     * Key format: permissions:userId:clinicId
     */
    public Set<String> getPermissions(String userId, String clinicId) {
        String cacheKey = buildCacheKey(userId, clinicId);
        try {
            @SuppressWarnings("unchecked")
            Set<String> cached = (Set<String>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.debug("Permission cache hit for user: {} clinic: {}", userId, clinicId);
                return cached;
            }
        } catch (Exception e) {
            log.warn("Error retrieving from permission cache", e);
        }
        return null;
    }

    /**
     * Cache permissions for a user in a clinic
     */
    public void cachePermissions(String userId, String clinicId, Set<String> permissions) {
        String cacheKey = buildCacheKey(userId, clinicId);
        try {
            redisTemplate.opsForValue().set(
                cacheKey, 
                permissions, 
                CACHE_TTL_MINUTES, 
                TimeUnit.MINUTES
            );
            log.debug("Cached {} permissions for user: {} clinic: {}", 
                permissions.size(), userId, clinicId);
        } catch (Exception e) {
            log.warn("Error caching permissions", e);
            // Don't fail the request if caching fails
        }
    }

    /**
     * Invalidate cache for a user in a clinic
     */
    public void invalidatePermissions(String userId, String clinicId) {
        String cacheKey = buildCacheKey(userId, clinicId);
        try {
            Boolean deleted = redisTemplate.delete(cacheKey);
            if (deleted != null && deleted) {
                log.debug("Invalidated permission cache for user: {} clinic: {}", userId, clinicId);
            }
        } catch (Exception e) {
            log.warn("Error invalidating permission cache", e);
        }
    }

    /**
     * Invalidate cache for a user across all clinics
     */
    public void invalidateUserPermissions(String userId) {
        try {
            String pattern = PERMISSION_CACHE_KEY_PREFIX + userId + ":*";
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Invalidated permission cache for user: {} across {} clinics", userId, keys.size());
            }
        } catch (Exception e) {
            log.warn("Error invalidating user permission cache", e);
        }
    }

    private String buildCacheKey(String userId, String clinicId) {
        return PERMISSION_CACHE_KEY_PREFIX + userId + ":" + clinicId;
    }
}
