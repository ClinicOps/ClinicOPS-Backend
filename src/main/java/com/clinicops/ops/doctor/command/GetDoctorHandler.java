package com.clinicops.ops.doctor.command;

import com.clinicops.application.command.CommandHandler;
import com.clinicops.ops.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetDoctorHandler implements CommandHandler<GetDoctorCommand> {

    private final DoctorService doctorService;

    @Override
    public void handle(GetDoctorCommand command) {

        command.setResult(
                doctorService.getDoctor(
                        command.getClinicId(),
                        command.getClinicDoctorId()
                )
        );
    }
}