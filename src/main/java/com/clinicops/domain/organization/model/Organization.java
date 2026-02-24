package com.clinicops.domain.organization.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.clinicops.common.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "organizations")
@CompoundIndexes({
    @CompoundIndex(name = "org_code_unique", def = "{'code':1}", unique = true)
})
@Getter
@Setter
public class Organization extends BaseEntity {

    private String name;

    private String code;

    private OrganizationStatus status;

    private boolean deleted;
}
