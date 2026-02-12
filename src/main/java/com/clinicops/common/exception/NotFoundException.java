package com.clinicops.common.exception;

public class NotFoundException extends BusinessException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotFoundException(String message) {
        super(message);
    }
}
