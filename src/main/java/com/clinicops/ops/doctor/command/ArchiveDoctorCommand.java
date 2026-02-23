package com.clinicops.ops.doctor.command;

import com.clinicops.application.command.Command;
import org.bson.types.ObjectId;

public class ArchiveDoctorCommand implements Command {

    private final ObjectId clinicId;
    private final ObjectId clinicDoctorId;

    public ArchiveDoctorCommand(ObjectId clinicId,
                                ObjectId clinicDoctorId) {
        this.clinicId = clinicId;
        this.clinicDoctorId = clinicDoctorId;
    }

    @Override
    public String domain() {
        return "OPS";
    }

    @Override
    public String resource() {
        return "DOCTOR";
    }

    @Override
    public String action() {
        return "ARCHIVE";
    }

    public ObjectId getClinicId() {
        return clinicId;
    }

    public ObjectId getClinicDoctorId() {
        return clinicDoctorId;
    }
}