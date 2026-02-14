package com.clinicops.web.ops;

import com.clinicops.domain.ops.service.PatientService;
import com.clinicops.web.ops.dto.CreatePatientRequest;
import com.clinicops.web.ops.dto.PatientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clinics/{clinicId}/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public PatientResponse create(@PathVariable String clinicId,
                                  @RequestBody CreatePatientRequest request) {
        return patientService.create(clinicId, request);
    }

    @GetMapping
    public Page<PatientResponse> list(@PathVariable String clinicId,
                                      Pageable pageable) {
        return patientService.list(clinicId, pageable);
    }
}
