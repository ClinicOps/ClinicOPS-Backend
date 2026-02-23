package com.clinicops.ops.doctor.command;

import com.clinicops.application.command.Command;
import com.clinicops.ops.doctor.dto.ChangeDoctorStatusRequest;
import org.bson.types.ObjectId;

public class ChangeDoctorStatusCommand implements Command {

    private final ObjectId clinicId;
    private final ObjectId clinicDoctorId;
    private final ChangeDoctorStatusRequest request;

    public ChangeDoctorStatusCommand(ObjectId clinicId,
                                     ObjectId clinicDoctorId,
                                     ChangeDoctorStatusRequest request) {
        this.clinicId = clinicId;
        this.clinicDoctorId = clinicDoctorId;
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
        return "STATUS_CHANGE";
    }

    public ObjectId getClinicId() {
        return clinicId;
    }

    public ObjectId getClinicDoctorId() {
        return clinicDoctorId;
    }

    public ChangeDoctorStatusRequest getRequest() {
        return request;
    }
}