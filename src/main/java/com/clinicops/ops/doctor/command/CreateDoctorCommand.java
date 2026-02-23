package com.clinicops.ops.doctor.command;

import com.clinicops.application.command.Command;
import com.clinicops.ops.doctor.dto.CreateDoctorRequest;
import com.clinicops.ops.doctor.dto.DoctorResponse;
import org.bson.types.ObjectId;

public class CreateDoctorCommand implements Command {

    private final ObjectId clinicId;
    private final CreateDoctorRequest request;

    private DoctorResponse result;

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

    public ObjectId getClinicId() {
        return clinicId;
    }

    public CreateDoctorRequest getRequest() {
        return request;
    }

    public DoctorResponse getResult() {
        return result;
    }

    public void setResult(DoctorResponse result) {
        this.result = result;
    }
}