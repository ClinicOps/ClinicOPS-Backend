package com.clinicops.ops.availability.controller;

import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.ops.availability.dto.SlotDTO;
import com.clinicops.ops.availability.model.DailySlotsDTO;
import com.clinicops.ops.availability.service.SlotGenerationService;
import com.clinicops.security.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ops/doctors")
@RequiredArgsConstructor
public class DoctorSlotController {

	private final SlotGenerationService slotGenerationService;

	@GetMapping("/{doctorId}/slots")
	public List<SlotDTO> getSlots(@PathVariable String doctorId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, HttpServletRequest request) {

        ObjectId clinicId = new ObjectId((String) request.getAttribute("CLINIC_ID"));

		return slotGenerationService.generateSlots(clinicId, new ObjectId(doctorId), date);
	}

	@GetMapping("/{doctorId}/calendar")
	public List<DailySlotsDTO> getCalendar(@PathVariable String doctorId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to, HttpServletRequest request) {

		ObjectId clinicId = new ObjectId((String) request.getAttribute("CLINIC_ID"));

		return slotGenerationService.generateCalendar(clinicId, new ObjectId(doctorId), from, to);
	}
}
