package com.clinicops.ops.doctor.command;

import org.springframework.stereotype.Component;

import com.clinicops.application.command.CommandHandler;
import com.clinicops.ops.doctor.service.DoctorService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BulkArchiveDoctorsHandler
        implements CommandHandler<BulkArchiveDoctorsCommand> {

    private final DoctorService doctorService;

    @Override
    public void handle(BulkArchiveDoctorsCommand command) {
        doctorService.bulkArchive(
                command.getClinicId(),
                command.getClinicDoctorIds()
        );
    }
}