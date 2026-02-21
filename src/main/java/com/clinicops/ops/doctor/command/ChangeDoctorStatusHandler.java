package com.clinicops.ops.doctor.command;

import org.springframework.stereotype.Component;

import com.clinicops.application.command.CommandHandler;
import com.clinicops.infra.messaging.EventPublisher;
import com.clinicops.ops.doctor.event.DoctorStatusChangedEvent;
import com.clinicops.ops.doctor.service.DoctorService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChangeDoctorStatusHandler
        implements CommandHandler<ChangeDoctorStatusCommand, Void> {

    private final DoctorService doctorService;
    private final EventPublisher eventPublisher;

    @Override
    public Void handle(ChangeDoctorStatusCommand command) {

        doctorService.changeStatus(
                command.clinicId(),
                command.clinicDoctorId(),
                command.request());

        eventPublisher.publish(new DoctorStatusChangedEvent(
                command.clinicId(),
                command.clinicDoctorId().toHexString()
        ));

        return null;
    }
}
