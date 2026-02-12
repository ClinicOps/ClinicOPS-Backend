package com.clinicops.modules.appointment.service;

import com.clinicops.modules.appointment.dto.AppointmentCancelRequest;
import com.clinicops.modules.appointment.dto.AppointmentCreateRequest;
import com.clinicops.modules.appointment.dto.AppointmentRescheduleRequest;
import com.clinicops.modules.appointment.dto.AppointmentResponse;
import com.clinicops.security.model.AuthUser;

public interface AppointmentService {

    AppointmentResponse bookAppointment(
        AuthUser user,
        AppointmentCreateRequest request
    );

    void cancelAppointment(
        AuthUser user,
        String appointmentId,
        AppointmentCancelRequest request
    );

    AppointmentResponse rescheduleAppointment(
        AuthUser user,
        String appointmentId,
        AppointmentRescheduleRequest request
    );
}
