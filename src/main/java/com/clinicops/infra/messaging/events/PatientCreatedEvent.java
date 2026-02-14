package com.clinicops.infra.messaging.events;

import com.clinicops.infra.messaging.BaseEvent;
import lombok.Getter;

@Getter
public class PatientCreatedEvent extends BaseEvent {

    private final String patientId;
    private final String clinicId;
    private final String patientCode;

    public PatientCreatedEvent(
            String patientId,
            String clinicId,
            String patientCode) {

        this.patientId = patientId;
        this.clinicId = clinicId;
        this.patientCode = patientCode;
    }
}
