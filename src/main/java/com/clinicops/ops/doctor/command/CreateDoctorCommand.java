package com.clinicops.ops.doctor.command;

import org.bson.types.ObjectId;

import com.clinicops.application.command.Command;
import com.clinicops.ops.doctor.dto.DoctorResponse;
import com.clinicops.ops.doctor.dto.CreateDoctorRequest;

public class CreateDoctorCommand implements Command {

    private final ObjectId clinicId;
    private final CreateDoctorRequest request;

    private DoctorResponse result; // 🔥 store output

    public CreateDoctorCommand(ObjectId clinicId,
                               CreateDoctorRequest request) {
        this.clinicId = clinicId;
        this.request = request;
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
        return "CREATE";
    }

    public ObjectId getClinicId() { return clinicId; }
    public CreateDoctorRequest getRequest() { return request; }

    public void setResult(DoctorResponse result) {
        this.result = result;
    }

    public DoctorResponse getResult() {
        return result;
    }
}
