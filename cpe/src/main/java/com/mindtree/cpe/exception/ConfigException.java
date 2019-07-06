package com.mindtree.cpe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ConfigException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConfigException() {
		// TODO Auto-generated constructor stub
	}

	public ConfigException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ConfigException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public ConfigException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
