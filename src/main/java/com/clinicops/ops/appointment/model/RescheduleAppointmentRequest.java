package com.clinicops.ops.appointment.model;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RescheduleAppointmentRequest {

    private LocalDate newDate;
    private LocalTime newStartTime;
}
