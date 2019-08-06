package com.connectplusplus.wizapi.models;

import org.springframework.http.HttpStatus;

/**
 * response object for DB response
 * */

public final class DBResponse <ResponseType> extends Response <ResponseType> {
	
	// constructors
	
	public DBResponse() {
		super();
	}
	
	public DBResponse(HttpStatus status, String message, ResponseType response) {
		super(status, message, response);
	}
}
