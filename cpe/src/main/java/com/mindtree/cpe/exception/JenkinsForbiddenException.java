package com.mindtree.cpe.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class JenkinsForbiddenException extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JenkinsForbiddenException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JenkinsForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public JenkinsForbiddenException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public JenkinsForbiddenException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public JenkinsForbiddenException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
