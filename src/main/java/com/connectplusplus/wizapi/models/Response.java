package com.connectplusplus.wizapi.models;

import org.springframework.http.HttpStatus;


/**
 * default response object
 * */

public class Response <ResponseType> {

	// fields
	
	private HttpStatus status;
	private String message;
	private ResponseType response;
	
	
	// constructors

	public Response() {}
	
	public Response(HttpStatus status, String message, ResponseType response) {
		this.status = status;
		this.message = message;
		this.response = response;
	}
	
	
	// getters & setters
	
	public HttpStatus getStatus() {
		return status;
	}
	public void setStatus(HttpStatus status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public ResponseType getResponse() {
		return response;
	}
	public void setResponse(ResponseType response) {
		this.response = response;
	}
}
