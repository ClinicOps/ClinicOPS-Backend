package com.clinicops.ops.doctor.command;

import org.springframework.stereotype.Component;

import com.clinicops.application.command.CommandHandler;
import com.clinicops.infra.messaging.EventPublisher;
import com.clinicops.ops.doctor.event.DoctorArchivedEvent;
import com.clinicops.ops.doctor.service.DoctorService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArchiveDoctorHandler
        implements CommandHandler<ArchiveDoctorCommand, Void> {

    private final DoctorService doctorService;
    private final EventPublisher eventPublisher;

    @Override
    public Void handle(ArchiveDoctorCommand command) {

        doctorService.archiveDoctor(
                command.clinicId(),
                command.clinicDoctorId());

        eventPublisher.publish(new DoctorArchivedEvent(
                command.clinicId(),
                command.clinicDoctorId().toHexString()
        ));

        return null;
    }
}
