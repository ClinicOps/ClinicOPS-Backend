package com.clinicops.ops.doctor.service;

import org.bson.types.ObjectId;

import com.clinicops.common.api.PageResponse;
import com.clinicops.ops.doctor.dto.ChangeDoctorStatusRequest;
import com.clinicops.ops.doctor.dto.CreateDoctorRequest;
import com.clinicops.ops.doctor.dto.DoctorResponse;
import com.clinicops.ops.doctor.dto.UpdateDoctorRequest;
import com.clinicops.ops.doctor.model.DoctorStatus;

public interface DoctorService {

    DoctorResponse createDoctor(ObjectId clinicId, CreateDoctorRequest request);

    DoctorResponse updateDoctor(ObjectId clinicId, ObjectId clinicDoctorId, UpdateDoctorRequest request);

    void changeStatus(ObjectId clinicId, ObjectId clinicDoctorId, ChangeDoctorStatusRequest request);

    void archiveDoctor(ObjectId clinicId, ObjectId clinicDoctorId);

    DoctorResponse getDoctor(ObjectId clinicId, ObjectId clinicDoctorId);

    PageResponse<DoctorResponse> listDoctors(
            ObjectId clinicId,
            String search,
            String specialization,
            DoctorStatus status,
            Boolean available,
            int page,
            int size);
}
