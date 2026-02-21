package com.clinicops.ops.doctor.command;

import org.bson.types.ObjectId;

import com.clinicops.application.command.Command;

public record ArchiveDoctorCommand(
        ObjectId clinicId,
        ObjectId clinicDoctorId
) implements Command<Void> {}
