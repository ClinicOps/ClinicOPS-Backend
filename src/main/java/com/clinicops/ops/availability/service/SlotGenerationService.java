package com.clinicops.ops.availability.service;

import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;

import com.clinicops.ops.availability.dto.SlotDTO;
import com.clinicops.ops.availability.model.DailySlotsDTO;

public interface SlotGenerationService {

	List<SlotDTO> generateSlots(ObjectId clinicId, ObjectId doctorId, LocalDate date);

	List<DailySlotsDTO> generateCalendar(ObjectId clinicId, ObjectId doctorId, LocalDate from, LocalDate to);

}
