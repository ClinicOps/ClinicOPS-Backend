package com.clinicops.common.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public abstract class BaseEntity {

    @Id
    private ObjectId id;

    @CreatedDate
    @Field("createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updatedAt")
    private Instant updatedAt;

    @CreatedBy
    private ObjectId createdBy;

    @LastModifiedBy
    private ObjectId updatedBy;

    // getters & setters
}
