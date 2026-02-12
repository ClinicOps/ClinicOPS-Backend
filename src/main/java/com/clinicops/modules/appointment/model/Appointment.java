package com.clinicops.modules.appointment.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "appointments")
@Data
@CompoundIndexes({
    @CompoundIndex(
        name = "doctor_time_idx",
        def = "{'doctorId':1, 'startTime':1, 'endTime':1}"
    )
})
public class Appointment {

    @Id
    private String id;

    private String workspaceId;

    private String doctorId;

    private String patientId;

    private Instant startTime;
    private Instant endTime;

    private AppointmentStatus status;

    private Instant createdAt;
}
