package com.clinicops.ops.doctor.command;

import com.clinicops.application.command.CommandHandler;
import com.clinicops.ops.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArchiveDoctorHandler
        implements CommandHandler<ArchiveDoctorCommand> {

    private final DoctorService doctorService;

    @Override
    public void handle(ArchiveDoctorCommand command) {

        doctorService.archiveDoctor(
                command.getClinicId(),
                command.getClinicDoctorId()
        );
    }
}