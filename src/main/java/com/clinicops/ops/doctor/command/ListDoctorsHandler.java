package com.clinicops.ops.doctor.command;

import com.clinicops.application.command.CommandHandler;
import com.clinicops.ops.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListDoctorsHandler
        implements CommandHandler<ListDoctorsCommand> {

    private final DoctorService doctorService;

    @Override
    public void handle(ListDoctorsCommand command) {

        command.setResult(
                doctorService.listDoctors(
                        command.getClinicId(),
                        command.getSearch(),
                        command.getSpecialization(),
                        command.getStatus(),
                        command.getAvailable(),
                        command.getPage(),
                        command.getSize()
                )
        );
    }
}