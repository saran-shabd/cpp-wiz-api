package com.connectplusplus.wizapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class to store all properties, keys and other secrets used in the application.
 * <br>
 * Loads all the properties from <b>application.properties</b>
 * */

@Component
public class Properties {
	
	/* * * * * * * * * * * * * * * * * * *
	 *  Encryption & Encoding Properties *
	 * * * * * * * * * * * * * * * * * * */

	@Value("${app.secretKey}")
	private String appSecret;
	
	@Value("${app.encryption.key}")
	private String encryptionKey;
	
	@Value("${app.encryption.iv}")
	private String encryptionIv;

	
	/* * * * * * * * * *
	 *  DB Properties  *
	 * * * * * * * * * */
	
	@Value("${app.db.service}")
	private String dbService;
	
	@Value("${app.db.host}")
	private String dbHost;
	
	@Value("${app.db.port}")
	private String dbPort;
	
	@Value("${app.db.name}")
	private String dbName;
	
	@Value("${app.db.user.name}")
	private String dbUser;
	
	@Value("${app.db.user.password}")
	private String dbPassword;
	
	
	/* * * * * * * * * * *
	 *  Getters/Setters  *
	 * * * * * * * * * * */
	
	public String getAppSecret() {
		return appSecret;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	
	public String getEncryptionKey() {
		return encryptionKey;
	}
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	
	public String getEncryptionIv() {
		return encryptionIv;
	}
	public void setEncryptionIv(String encryptionIv) {
		this.encryptionIv = encryptionIv;
	}
	
	public String getDbService() {
		return dbService;
	}
	public void setDbService(String dbService) {
		this.dbService = dbService;
	}
	
	public String getDbHost() {
		return dbHost;
	}
	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}
	
	public String getDbPort() {
		return dbPort;
	}
	public void setDbPort(String dbPort) {
		this.dbPort = dbPort;
	}
	
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
}
