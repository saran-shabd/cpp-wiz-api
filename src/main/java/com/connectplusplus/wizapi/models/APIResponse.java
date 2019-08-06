package com.connectplusplus.wizapi.models;

import org.springframework.http.HttpStatus;

/**
 * response object for API responses
 * */

public final class APIResponse <ResponseType> extends Response <ResponseType> {
	
	// constructors
	
	public APIResponse() {
		super();
	}
	
	public APIResponse(HttpStatus status, String message, ResponseType response) {
		super(status, message, response);
	}
}
