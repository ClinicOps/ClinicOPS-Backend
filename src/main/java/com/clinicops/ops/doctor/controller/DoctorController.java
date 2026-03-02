package com.clinicops.ops.doctor.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
import com.clinicops.common.exception.AuthorizationException;
import com.clinicops.ops.doctor.command.ArchiveDoctorCommand;
import com.clinicops.ops.doctor.command.ArchiveDoctorHandler;
import com.clinicops.ops.doctor.command.BulkArchiveDoctorsCommand;
import com.clinicops.ops.doctor.command.BulkArchiveDoctorsHandler;
import com.clinicops.ops.doctor.command.ChangeDoctorStatusCommand;
import com.clinicops.ops.doctor.command.ChangeDoctorStatusHandler;
import com.clinicops.ops.doctor.command.CreateDoctorCommand;
import com.clinicops.ops.doctor.command.CreateDoctorHandler;
import com.clinicops.ops.doctor.command.GetDoctorCommand;
import com.clinicops.ops.doctor.command.GetDoctorHandler;
import com.clinicops.ops.doctor.command.ListDoctorsCommand;
import com.clinicops.ops.doctor.command.ListDoctorsHandler;
import com.clinicops.ops.doctor.command.UpdateDoctorCommand;
import com.clinicops.ops.doctor.command.UpdateDoctorHandler;
import com.clinicops.ops.doctor.dto.ChangeDoctorStatusRequest;
import com.clinicops.ops.doctor.dto.CreateDoctorRequest;
import com.clinicops.ops.doctor.dto.DoctorResponse;
import com.clinicops.ops.doctor.dto.UpdateDoctorRequest;
import com.clinicops.ops.doctor.model.DoctorStatus;
import com.clinicops.ops.doctor.service.DoctorService;
import com.clinicops.security.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ops/doctors")
@RequiredArgsConstructor
public class DoctorController {

	private final CommandGateway commandGateway;

	private final CreateDoctorHandler createDoctorHandler;
	private final UpdateDoctorHandler updateDoctorHandler;
	private final ChangeDoctorStatusHandler changeDoctorStatusHandler;
	private final ArchiveDoctorHandler archiveDoctorHandler;
	private final GetDoctorHandler getDoctorHandler;
	private final ListDoctorsHandler listDoctorsHandler;
	private final BulkArchiveDoctorsHandler bulkArchiveDoctorsHandler;

	private final DoctorService doctorService;

	@PostMapping
	public ApiResponse<DoctorResponse> create(
			@Valid @RequestBody CreateDoctorRequest request, HttpServletRequest httpRequest)
			throws AuthorizationException {

		ObjectId clinicId = new ObjectId((String) httpRequest.getAttribute("CLINIC_ID"));
		CreateDoctorCommand command = new CreateDoctorCommand(clinicId, request);

		commandGateway.execute(command, httpRequest, createDoctorHandler);

		return ApiResponse.ok(command.getResult());
	}

	@PutMapping("/{id}")
	public ApiResponse<DoctorResponse> update(@PathVariable String id,
			@Valid @RequestBody UpdateDoctorRequest request, HttpServletRequest httpRequest)
			throws AuthorizationException {

		ObjectId clinicId = new ObjectId((String) httpRequest.getAttribute("CLINIC_ID"));
		UpdateDoctorCommand command = new UpdateDoctorCommand(clinicId, new ObjectId(id), request);

		commandGateway.execute(command, httpRequest, updateDoctorHandler);

		return ApiResponse.ok(command.getResult());
	}

	@PatchMapping("/{id}/status")
	public ApiResponse<Void> changeStatus(@PathVariable String id,
			@Valid @RequestBody ChangeDoctorStatusRequest request, HttpServletRequest httpRequest)
			throws AuthorizationException {

		ObjectId clinicId = new ObjectId((String) httpRequest.getAttribute("CLINIC_ID"));
		ChangeDoctorStatusCommand command = new ChangeDoctorStatusCommand(clinicId, new ObjectId(id), request);

		commandGateway.execute(command, httpRequest, changeDoctorStatusHandler);

		return ApiResponse.ok();
	}

	@DeleteMapping("/{id}")
	public ApiResponse<Void> archive(@PathVariable String id,
			HttpServletRequest httpRequest) throws AuthorizationException {

		ObjectId clinicId = new ObjectId((String) httpRequest.getAttribute("CLINIC_ID"));
		ArchiveDoctorCommand command = new ArchiveDoctorCommand(clinicId, new ObjectId(id));

		commandGateway.execute(command, httpRequest, archiveDoctorHandler);

		return ApiResponse.ok();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('OPS_DOCTOR_VIEW')")
	public ApiResponse<DoctorResponse> get(@PathVariable String id,
			HttpServletRequest request) throws AuthorizationException {

		ObjectId clinicId = new ObjectId((String) request.getAttribute("CLINIC_ID"));
		GetDoctorCommand command = new GetDoctorCommand(clinicId, new ObjectId(id));

		commandGateway.execute(command, request, getDoctorHandler);

		return ApiResponse.ok(command.getResult());
	}

	@GetMapping
	@PreAuthorize("hasAuthority('OPS_DOCTOR_VIEW')")
	public ApiResponse<PageResponse<DoctorResponse>> list(
			@RequestParam(required = false) String search, @RequestParam(required = false) String specialization,
			@RequestParam(required = false) DoctorStatus status, @RequestParam(required = false) Boolean available,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			HttpServletRequest request) throws AuthorizationException {

		ObjectId clinicId = new ObjectId((String) request.getAttribute("CLINIC_ID"));
		ListDoctorsCommand command = new ListDoctorsCommand(clinicId, search, specialization, status,
				available, page, size);

		commandGateway.execute(command, request, listDoctorsHandler);

		return ApiResponse.ok(command.getResult());
	}

	@PostMapping("/bulk-archive")
	@PreAuthorize("hasAuthority('OPS_DOCTOR_ARCHIVE')")
	public ApiResponse<Void> bulkArchive(@RequestBody List<String> ids,
			HttpServletRequest request) throws AuthorizationException {

		ObjectId clinicId = new ObjectId((String) request.getAttribute("CLINIC_ID"));
		List<ObjectId> objectIds = ids.stream().map(ObjectId::new).toList();

		BulkArchiveDoctorsCommand command = new BulkArchiveDoctorsCommand(clinicId, objectIds);

		commandGateway.execute(command, request, bulkArchiveDoctorsHandler);

		return ApiResponse.ok();
	}

	@GetMapping("/export")
	@PreAuthorize("hasAuthority('OPS_DOCTOR_VIEW')")
	public void export(HttpServletResponse response, HttpServletRequest request) throws IOException {

		ObjectId clinicId = new ObjectId((String) request.getAttribute("CLINIC_ID"));
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=doctors.csv");

		List<DoctorResponse> doctors = doctorService.exportDoctors(clinicId);

		PrintWriter writer = response.getWriter();

		writer.println("Name,License,Status,Available,Specializations,Fee");

		for (DoctorResponse d : doctors) {
			writer.printf("%s %s,%s,%s,%s,%s,%d%n", d.getFirstName(), d.getLastName(), d.getLicenseNumber(),
					d.getStatus(), d.getAvailable(), String.join("|", d.getSpecializations()), d.getConsultationFee());
		}

		writer.flush();
	}
}
