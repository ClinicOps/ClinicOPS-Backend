package com.clinicops.ops.availability.model;

import java.time.DayOfWeek;
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

@Document("doctor_availability")
@CompoundIndexes({
    @CompoundIndex(
        name = "clinic_doctor_day_idx",
        def = "{'clinicId':1, 'doctorId':1, 'dayOfWeek':1, 'isActive':1}"
    )
})
@Getter
@Setter
public class DoctorAvailability extends BaseEntity {

    private ObjectId clinicId;
    private ObjectId doctorId;

    private DayOfWeek dayOfWeek;

    private LocalTime startTime;
    private LocalTime endTime;

    private Integer slotDurationMinutes;
    private Integer bufferMinutes;   // new

    private LocalDate validFrom;
    private LocalDate validTo;

    private Boolean isActive = true;

    @Version
    private Long version;
}
