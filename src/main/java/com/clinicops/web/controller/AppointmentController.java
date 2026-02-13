package com.clinicops.web.controller;

import com.clinicops.application.command.CommandGateway;
import com.clinicops.application.command.appointment.*;
import com.clinicops.domain.ops.service.AppointmentService;
import jakarta.servlet.http.HttpServletRequest;
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
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        CreateAppointmentCommand cmd =
                new CreateAppointmentCommand(
                        body.get("patientName"),
                        Instant.parse(body.get("scheduledAt"))
                );

        gateway.execute(cmd, request, c ->
                createHandler.handle(cmd,
                        (String) request.getAttribute("CLINIC_ID")));
    }

    @DeleteMapping("/{id}")
    public void cancel(
            @PathVariable String id,
            HttpServletRequest request) {

        CancelAppointmentCommand cmd =
                new CancelAppointmentCommand(id);

        gateway.execute(cmd, request, c ->
                cancelHandler.handle(cmd,
                        (String) request.getAttribute("CLINIC_ID")));
    }

    @GetMapping
    public Object list(HttpServletRequest request) {
        String clinicId =
                (String) request.getAttribute("CLINIC_ID");

        return queryService.list(clinicId);
    }
}
