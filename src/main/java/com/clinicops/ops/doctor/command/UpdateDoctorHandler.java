package com.clinicops.ops.doctor.command;

import org.springframework.stereotype.Component;

import com.clinicops.application.command.CommandHandler;
import com.clinicops.infra.messaging.EventPublisher;
import com.clinicops.ops.doctor.dto.DoctorResponse;
import com.clinicops.ops.doctor.event.DoctorUpdatedEvent;
import com.clinicops.ops.doctor.service.DoctorService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UpdateDoctorHandler
        implements CommandHandler<UpdateDoctorCommand, DoctorResponse> {

    private final DoctorService doctorService;
    private final EventPublisher eventPublisher;

    @Override
    public DoctorResponse handle(UpdateDoctorCommand command) {

        DoctorResponse response =
                doctorService.updateDoctor(
                        command.clinicId(),
                        command.clinicDoctorId(),
                        command.request());

        eventPublisher.publish(new DoctorUpdatedEvent(
                command.clinicId(),
                command.clinicDoctorId().toHexString()
        ));

        return response;
    }
}
