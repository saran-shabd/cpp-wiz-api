package com.connectplusplus.wizapi.utils;

import java.sql.Timestamp;

import com.connectplusplus.wizapi.models.UserAccessToken;


/**
 * class containing common utility methods
 * */

public class CommonUtils {

	/**
	 * utility method to generate user access token (encrypted)
	 * */
	public static String generateUserAccessToken(String regno, String firstname, String lastname, String email, String password, Timestamp currTimestamp) {
		
		UserAccessToken userAccessToken = new UserAccessToken(regno, firstname, lastname, email, null, password, currTimestamp);
		
		String token = EncodingUtils.encodePojo(userAccessToken);
		
		// encrypt user access token
		String encryptedToken = EncryptionUtils.encrypt(token);
		
		return encryptedToken;
	}
	
	/**
	 * utility method to decode an user access token
	 * */
	public static UserAccessToken decodeUserAccessToken(String token) {
		
		String decryptedToken = EncryptionUtils.decrypt(token);
		
		UserAccessToken userAccessToken = EncodingUtils.decodePojo(decryptedToken, UserAccessToken.class);
		
		return userAccessToken;
	}
}
