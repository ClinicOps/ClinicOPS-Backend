package com.clinicops.ops.patient.counter;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("patient_counters")
public class PatientCounter {

	@Id
	private ObjectId id; // clinicId acts as id

	private long sequence;

	protected PatientCounter() {
	}

	public PatientCounter(ObjectId clinicId) {
		this.id = clinicId;
		this.sequence = 0L;
	}
	
	public long getSequence() {
	    return sequence;
	}

	public long next() {
		return ++sequence;
	}
}
