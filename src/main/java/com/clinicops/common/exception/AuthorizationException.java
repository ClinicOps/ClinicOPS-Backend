package com.clinicops.common.exception;

public class AuthorizationException extends Exception { // or RuntimeException
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public AuthorizationException(String message) {
        super(message);
    }
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}