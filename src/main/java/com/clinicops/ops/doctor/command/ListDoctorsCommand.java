package com.clinicops.ops.doctor.command;

import com.clinicops.application.command.Command;
import com.clinicops.common.api.PageResponse;
import com.clinicops.ops.doctor.dto.DoctorResponse;
import com.clinicops.ops.doctor.model.DoctorStatus;
import org.bson.types.ObjectId;

public class ListDoctorsCommand implements Command {

    private final ObjectId clinicId;
    private final String search;
    private final String specialization;
    private final DoctorStatus status;
    private final Boolean available;
    private final int page;
    private final int size;

    private PageResponse<DoctorResponse> result;

    public ListDoctorsCommand(
            ObjectId clinicId,
            String search,
            String specialization,
            DoctorStatus status,
            Boolean available,
            int page,
            int size) {

        this.clinicId = clinicId;
        this.search = search;
        this.specialization = specialization;
        this.status = status;
        this.available = available;
        this.page = page;
        this.size = size;
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

    public ObjectId getClinicId() { return clinicId; }
    public String getSearch() { return search; }
    public String getSpecialization() { return specialization; }
    public DoctorStatus getStatus() { return status; }
    public Boolean getAvailable() { return available; }
    public int getPage() { return page; }
    public int getSize() { return size; }

    public PageResponse<DoctorResponse> getResult() { return result; }
    public void setResult(PageResponse<DoctorResponse> result) { this.result = result; }
}