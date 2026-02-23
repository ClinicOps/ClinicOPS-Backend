package com.clinicops.ops.availability.model;

import java.time.LocalDate;
import java.time.LocalTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.clinicops.common.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Document("doctor_availability_exceptions")
@CompoundIndexes({
    @CompoundIndex(
        name = "clinic_doctor_date_idx",
        def = "{'clinicId':1, 'doctorId':1, 'date':1}"
    )
})
@Getter
@Setter
public class DoctorAvailabilityException extends BaseEntity {

    private ObjectId clinicId;
    private ObjectId doctorId;

    private LocalDate date;

    private LocalTime startTime;   // nullable = full day
    private LocalTime endTime;     // nullable = full day

    private ExceptionType type;    // BLOCKED, EXTRA, LEAVE
    private String reason;

    @Version
    private Long version;
}
