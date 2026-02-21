package com.clinicops.ops.doctor.command;

import org.springframework.stereotype.Component;

import com.clinicops.application.command.CommandHandler;
import com.clinicops.ops.doctor.dto.DoctorResponse;
import com.clinicops.ops.doctor.service.DoctorService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateDoctorHandler
        implements CommandHandler<CreateDoctorCommand> {

    private final DoctorService doctorService;

    @Override
    public void handle(CreateDoctorCommand command) {

        DoctorResponse response =
                doctorService.createDoctor(
                        command.getClinicId(),
                        command.getRequest());

        command.setResult(response);
    }
}
