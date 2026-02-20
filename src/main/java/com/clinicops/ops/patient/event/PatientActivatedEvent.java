package com.clinicops.ops.patient.event;

import com.clinicops.infra.messaging.BaseEvent;
import lombok.Getter;

@Getter
public class PatientActivatedEvent extends BaseEvent {

    private final String patientId;
    private final String clinicId;

    public PatientActivatedEvent(
            String patientId,
            String clinicId) {

        this.patientId = patientId;
        this.clinicId = clinicId;
    }
}
