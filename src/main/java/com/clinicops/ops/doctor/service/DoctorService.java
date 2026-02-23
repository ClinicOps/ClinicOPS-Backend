package com.clinicops.ops.doctor.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.clinicops.common.api.PageResponse;
import com.clinicops.ops.doctor.dto.ChangeDoctorStatusRequest;
import com.clinicops.ops.doctor.dto.CreateDoctorRequest;
import com.clinicops.ops.doctor.dto.DoctorResponse;
import com.clinicops.ops.doctor.dto.UpdateDoctorRequest;
import com.clinicops.ops.doctor.model.DoctorStatus;

@Service
public interface DoctorService {

    public DoctorResponse createDoctor(ObjectId clinicId, CreateDoctorRequest request);

    public DoctorResponse updateDoctor(ObjectId clinicId, ObjectId clinicDoctorId, UpdateDoctorRequest request);

    public void changeStatus(ObjectId clinicId, ObjectId clinicDoctorId, ChangeDoctorStatusRequest request);

    public void archiveDoctor(ObjectId clinicId, ObjectId clinicDoctorId);

    public DoctorResponse getDoctor(ObjectId clinicId, ObjectId clinicDoctorId);

    public PageResponse<DoctorResponse> listDoctors(
            ObjectId clinicId,
            String search,
            String specialization,
            DoctorStatus status,
            Boolean available,
            int page,
            int size);
    
    public void bulkArchive(ObjectId clinicId, List<ObjectId> ids);
    
    public List<DoctorResponse> exportDoctors(ObjectId clinicId);
    
}
