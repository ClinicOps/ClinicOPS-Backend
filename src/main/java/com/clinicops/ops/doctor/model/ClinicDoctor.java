package com.clinicops.ops.doctor.model;

import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.clinicops.common.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Document("clinic_doctors")
@CompoundIndexes({
    @CompoundIndex(name = "clinic_doctor_unique",
        def = "{'clinicId':1, 'doctorId':1}", unique = true),
    @CompoundIndex(name = "clinic_specialization_idx",
        def = "{'clinicId':1, 'specializations':1}"),
    @CompoundIndex(name = "clinic_status_idx",
        def = "{'clinicId':1, 'status':1}")
})
@Getter
@Setter
public class ClinicDoctor extends BaseEntity {

    @Id
    private ObjectId id;

    private ObjectId clinicId;
    private ObjectId doctorId;

    private List<String> specializations;

    private Integer consultationFee;

    private DoctorStatus status;

    private Boolean available = true;

    private Boolean archived = false;

    private LocalDate visitingFrom;
    private LocalDate visitingTo;

    @Version
    private Long version;
}