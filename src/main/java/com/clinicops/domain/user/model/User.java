package com.clinicops.domain.user.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.clinicops.common.model.BaseEntity;

@Data
@Document(collection = "users")
public class User extends BaseEntity{

    @Indexed(unique = true)
    private String email;

    private String password;

    private boolean active;

    private boolean deleted;
}
