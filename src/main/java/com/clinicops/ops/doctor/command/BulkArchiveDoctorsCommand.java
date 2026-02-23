package com.clinicops.ops.doctor.command;

import com.clinicops.application.command.Command;
import org.bson.types.ObjectId;

import java.util.List;

public class BulkArchiveDoctorsCommand implements Command {

    private final ObjectId clinicId;
    private final List<ObjectId> clinicDoctorIds;

    public BulkArchiveDoctorsCommand(
            ObjectId clinicId,
            List<ObjectId> clinicDoctorIds) {
        this.clinicId = clinicId;
        this.clinicDoctorIds = clinicDoctorIds;
    }

    @Override public String domain() { return "OPS"; }
    @Override public String resource() { return "DOCTOR"; }
    @Override public String action() { return "ARCHIVE"; }

    public ObjectId getClinicId() { return clinicId; }
    public List<ObjectId> getClinicDoctorIds() { return clinicDoctorIds; }
}