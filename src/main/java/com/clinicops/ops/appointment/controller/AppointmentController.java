package com.clinicops.ops.appointment.controller;

import com.clinicops.application.command.CommandGateway;
import com.clinicops.common.exception.AuthorizationException;
import com.clinicops.ops.appointment.command.CancelAppointmentCommand;
import com.clinicops.ops.appointment.command.CancelAppointmentHandler;
import com.clinicops.ops.appointment.command.CreateAppointmentCommand;
import com.clinicops.ops.appointment.command.CreateAppointmentHandler;
import com.clinicops.ops.appointment.dto.CreateAppointmentRequest;
import com.clinicops.ops.appointment.service.AppointmentService;
import com.clinicops.security.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/ops/appointments")
public class AppointmentController {

    private final CommandGateway gateway;
    private final CreateAppointmentHandler createHandler;
    private final CancelAppointmentHandler cancelHandler;
    private final AppointmentService queryService;

    public AppointmentController(
            CommandGateway gateway,
            CreateAppointmentHandler createHandler,
            CancelAppointmentHandler cancelHandler,
            AppointmentService queryService) {

        this.gateway = gateway;
        this.createHandler = createHandler;
        this.cancelHandler = cancelHandler;
        this.queryService = queryService;
    }

    @PostMapping
    public void create(
    		@RequestBody CreateAppointmentRequest req,
            HttpServletRequest request) throws AuthorizationException {

    	 CreateAppointmentCommand cmd =
    		        new CreateAppointmentCommand(
    		            req.patientId(),
    		            req.scheduledAt()
    		        );

    		    gateway.execute(cmd, request, c ->
    		        createHandler.handle(cmd, SecurityUtils.getCurrentClinicId()));
    		}

    @DeleteMapping("/{id}")
    public void cancel(
            @PathVariable String id,
            HttpServletRequest request) throws AuthorizationException {

        CancelAppointmentCommand cmd =
                new CancelAppointmentCommand(id);

        gateway.execute(cmd, request, c ->
                cancelHandler.handle(cmd, SecurityUtils.getCurrentClinicId()));
    } 

    @GetMapping
    public Object list(HttpServletRequest request) {
        ObjectId clinicId = SecurityUtils.getCurrentClinicId();

        return queryService.list(clinicId);
    }
}
