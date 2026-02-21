package com.clinicops.ops.doctor.controller;

import org.bson.types.ObjectId;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.application.command.CommandGateway;
import com.clinicops.common.api.ApiResponse;
import com.clinicops.common.api.PageResponse;
import com.clinicops.ops.doctor.command.ArchiveDoctorCommand;
import com.clinicops.ops.doctor.command.ChangeDoctorStatusCommand;
import com.clinicops.ops.doctor.command.CreateDoctorCommand;
import com.clinicops.ops.doctor.command.UpdateDoctorCommand;
import com.clinicops.ops.doctor.dto.ChangeDoctorStatusRequest;
import com.clinicops.ops.doctor.dto.CreateDoctorRequest;
import com.clinicops.ops.doctor.dto.DoctorResponse;
import com.clinicops.ops.doctor.dto.UpdateDoctorRequest;
import com.clinicops.ops.doctor.model.DoctorStatus;
import com.clinicops.ops.doctor.service.DoctorService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clinics/{clinicId}/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final CommandGateway commandGateway;
    private final DoctorService doctorService;
    
    @PostMapping
    public ApiResponse<DoctorResponse> create(
            @PathVariable String clinicId,
            @Valid @RequestBody CreateDoctorRequest request,
            HttpServletRequest httpRequest) {

        CreateDoctorCommand command =
                new CreateDoctorCommand(
                        new ObjectId(clinicId),
                        request);

        commandGateway.execute(
                command,
                httpRequest,
                createDoctorHandler);

        return ApiResponse.ok(command.getResult());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('OPS_DOCTOR_UPDATE')")
    public ApiResponse<DoctorResponse> update(
            @PathVariable String clinicId,
            @PathVariable String id,
            @Valid @RequestBody UpdateDoctorRequest request) {

        DoctorResponse response =
                commandGateway.dispatch(
                        new UpdateDoctorCommand(
                                new ObjectId(clinicId),
                                new ObjectId(id),
                                request));

        return ApiResponse.success(response);
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('OPS_DOCTOR_STATUS_CHANGE')")
    public ApiResponse<Void> changeStatus(
            @PathVariable String clinicId,
            @PathVariable String id,
            @Valid @RequestBody ChangeDoctorStatusRequest request) {

        commandGateway.dispatch(
                new ChangeDoctorStatusCommand(
                        new ObjectId(clinicId),
                        new ObjectId(id),
                        request));

        return ApiResponse.success();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OPS_DOCTOR_ARCHIVE')")
    public ApiResponse<Void> archive(
            @PathVariable String clinicId,
            @PathVariable String id) {

        commandGateway.dispatch(
                new ArchiveDoctorCommand(
                        new ObjectId(clinicId),
                        new ObjectId(id)));

        return ApiResponse.success();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OPS_DOCTOR_VIEW')")
    public ApiResponse<DoctorResponse> get(
            @PathVariable String clinicId,
            @PathVariable String id) {

        return ApiResponse.success(
                doctorService.getDoctor(
                        new ObjectId(clinicId),
                        new ObjectId(id)));
    }
    
    @GetMapping
    @PreAuthorize("hasAuthority('OPS_DOCTOR_VIEW')")
    public ApiResponse<PageResponse<DoctorResponse>> list(
            @PathVariable String clinicId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) DoctorStatus status,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.success(
                doctorService.listDoctors(
                        new ObjectId(clinicId),
                        search,
                        specialization,
                        status,
                        available,
                        page,
                        size));
    }
}
