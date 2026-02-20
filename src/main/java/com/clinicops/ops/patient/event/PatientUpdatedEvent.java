package com.clinicops.infra.messaging.events;

import com.clinicops.infra.messaging.BaseEvent;
import lombok.Getter;

@Getter
public class PatientUpdatedEvent extends BaseEvent {

    private final String patientId;
    private final String clinicId;

    public PatientUpdatedEvent(
            String patientId,
            String clinicId) {

        this.patientId = patientId;
        this.clinicId = clinicId;
    }
}
