package com.clinicops.ops.doctor.command;

import com.clinicops.application.command.Command;
import com.clinicops.ops.doctor.dto.DoctorResponse;
import org.bson.types.ObjectId;

public class GetDoctorCommand implements Command {

    private final ObjectId clinicId;
    private final ObjectId clinicDoctorId;

    private DoctorResponse result;

    public GetDoctorCommand(ObjectId clinicId, ObjectId clinicDoctorId) {
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
        return "VIEW";
    }

    public ObjectId getClinicId() {
        return clinicId;
    }

    public ObjectId getClinicDoctorId() {
        return clinicDoctorId;
    }

    public DoctorResponse getResult() {
        return result;
    }

    public void setResult(DoctorResponse result) {
        this.result = result;
    }
}