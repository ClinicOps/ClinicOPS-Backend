package com.clinicops.ops.doctor.command;

import org.bson.types.ObjectId;

import com.clinicops.application.command.Command;
import com.clinicops.ops.doctor.dto.DoctorResponse;
import com.clinicops.ops.doctor.dto.UpdateDoctorRequest;

public record UpdateDoctorCommand(
        ObjectId clinicId,
        ObjectId clinicDoctorId,
        UpdateDoctorRequest request
) implements Command<DoctorResponse> {}
