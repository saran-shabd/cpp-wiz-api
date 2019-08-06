package com.connectplusplus.wizapi.models;


/**
 * basic user information
 * */

public class UserInfo {

	// fields
	
	private String regno;
	private String firstname;
	private String lastname;
	private String email;
	private String userAccessToken;
	
	
	// constructors
	
	public UserInfo() {}
	
	public UserInfo(String regno, String firstname, String lastname, String email, String userAccessToken) {
		this.regno = regno;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.userAccessToken = userAccessToken;
	}
	
	
	// getters & setters
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String getRegno() {
		return regno;
	}
	public void setRegno(String regno) {
		this.regno = regno;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getUserAccessToken() {
		return userAccessToken;
	}
	public void setUserAccessToken(String userAccessToken) {
		this.userAccessToken = userAccessToken;
	}
}
