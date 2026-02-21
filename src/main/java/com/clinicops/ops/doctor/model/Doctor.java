package com.clinicops.ops.doctor.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.clinicops.common.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Document("doctors")
@Getter
@Setter
public class Doctor extends BaseEntity {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String licenseNumber;

    private String firstName;
    private String lastName;

    private String phone;
    private String email;

    private List<String> qualifications;

    private String profileImageUrl;

    @Version
    private Long version;
}
