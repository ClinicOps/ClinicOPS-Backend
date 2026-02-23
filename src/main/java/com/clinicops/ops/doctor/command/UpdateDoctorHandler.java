package com.clinicops.ops.doctor.command;

import com.clinicops.application.command.CommandHandler;
import com.clinicops.ops.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateDoctorHandler
        implements CommandHandler<UpdateDoctorCommand> {

    private final DoctorService doctorService;

    @Override
    public void handle(UpdateDoctorCommand command) {

        command.setResult(
                doctorService.updateDoctor(
                        command.getClinicId(),
                        command.getClinicDoctorId(),
                        command.getRequest()
                )
        );
    }
}