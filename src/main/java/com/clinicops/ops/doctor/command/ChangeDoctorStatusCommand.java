package com.clinicops.ops.doctor.command;

import org.bson.types.ObjectId;

import com.clinicops.application.command.Command;
import com.clinicops.ops.doctor.dto.ChangeDoctorStatusRequest;

public record ChangeDoctorStatusCommand(
        ObjectId clinicId,
        ObjectId clinicDoctorId,
        ChangeDoctorStatusRequest request
) implements Command<Void> {}
