package com.connectplusplus.wizapi.models;

import java.sql.Timestamp;


/**
 * user information required for generating user access token
 * */

public final class UserAccessToken extends UserInfo {

	// fields

	private String password;
	private Timestamp registrationTimeStamp;

	
	// constructors
	
	public UserAccessToken() {}
	
	public UserAccessToken(
				String regno, String firstname, String lastname, 
				String email, String userAccessToken,
				String password, Timestamp registrationTimeStamp
			) {
		
		super(regno, firstname, lastname, email, userAccessToken);
		
		this.password = password;
		this.registrationTimeStamp = registrationTimeStamp;
	}
	
	
	// getters & setters

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Timestamp getRegistrationTimeStamp() {
		return registrationTimeStamp;
	}
	public void setRegistrationTimeStamp(Timestamp registrationTimeStamp) {
		this.registrationTimeStamp = registrationTimeStamp;
	}
}
