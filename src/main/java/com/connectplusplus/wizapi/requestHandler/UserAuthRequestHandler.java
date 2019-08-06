package com.connectplusplus.wizapi.requestHandler;

import org.springframework.http.HttpStatus;

import com.connectplusplus.wizapi.dao.UserDAO;
import com.connectplusplus.wizapi.models.APIResponse;
import com.connectplusplus.wizapi.models.DBResponse;
import com.connectplusplus.wizapi.models.UserAccessToken;
import com.connectplusplus.wizapi.models.UserInfo;
import com.connectplusplus.wizapi.utils.EncodingUtils;
import com.connectplusplus.wizapi.utils.EncryptionUtils;
import com.connectplusplus.wizapi.utils.StringUtils;

public class UserAuthRequestHandler {
	
	/**
	 * utility method to validate an user access token
	 * */
	public static APIResponse<Boolean> isUserAccessTokenValid(String userAccessToken) {
		
		APIResponse<Boolean> apiResponse = new APIResponse<>();
		
		try {
			// check for empty field
			
			boolean isEmpty = StringUtils.isEmpty(userAccessToken);
			
			if (!isEmpty) {
				
				// decrypt token
				String decryptedUserAccessToken = EncryptionUtils.decrypt(userAccessToken);
				
				if (null != decryptedUserAccessToken) {
					
					// decode token
					UserAccessToken token = EncodingUtils.decodePojo(decryptedUserAccessToken, UserAccessToken.class);
					
					
					// validate user access token from DB		
					DBResponse<Boolean> dbResponse = UserDAO.validateUserAccessToken(token.getRegno(), userAccessToken);
					
					apiResponse.setStatus(dbResponse.getStatus());
					apiResponse.setMessage(dbResponse.getMessage());
					apiResponse.setResponse(dbResponse.getResponse());
					
				} else { throw new Exception(); }
				
			} else { throw new Exception(); }
			
		} catch (Exception e) {  // handle exceptions for invalid token formats
			
			// invalid user access token
			
			apiResponse.setStatus(HttpStatus.UNAUTHORIZED);
			apiResponse.setMessage("Invalid Arguments");
			apiResponse.setResponse(false);
		}
		
		
		return apiResponse;
	}

	public static APIResponse<UserInfo> registerNewUser(String firstname, String lastname, String regno, String password) {
		
		APIResponse<UserInfo> apiResponse = new APIResponse<>();
		
		
		// check for invalid arguments
		
		boolean areAnyEmptyFields = StringUtils.containsAnyEmptyString(new String[] { firstname, lastname, regno, password });
		boolean isValidRegno = StringUtils.isValidRegno(regno);
		boolean isStrongPassword = StringUtils.isStrongPassword(password);
		
		if (areAnyEmptyFields || !isValidRegno || !isStrongPassword) {
			
			// invalid arguments
			
			apiResponse.setStatus(HttpStatus.BAD_REQUEST);
			apiResponse.setMessage("Invalid Arguments");
			apiResponse.setResponse(null);
			
		} else {
			
			// hash password
			String hashedPassword = EncryptionUtils.getHash(password);
			
			
			// register the user
			
			DBResponse<UserInfo> dbResponse = UserDAO.registerNewUser(firstname, lastname, regno, hashedPassword);
			
			apiResponse.setStatus(dbResponse.getStatus());
			apiResponse.setMessage(dbResponse.getMessage());
			apiResponse.setResponse(dbResponse.getResponse());
		}
		
		
		return apiResponse;
	}

	public static APIResponse<UserInfo> loginUser(String regno, String password) {
		
		APIResponse<UserInfo> apiResponse = new APIResponse<>();
		
		
		// check for invalid arguments
		
		boolean areAnyEmptyFields = StringUtils.containsAnyEmptyString(new String[] { regno, password });
		boolean isValidRegno = StringUtils.isValidRegno(regno);
		
		if (areAnyEmptyFields || !isValidRegno) {
			
			// invalid arguments
			
			apiResponse.setStatus(HttpStatus.BAD_REQUEST);
			apiResponse.setMessage("Invalid Arguments");
			apiResponse.setResponse(null);
			
		} else {
			
			// login user
			
			DBResponse<UserInfo> dbResponse = UserDAO.loginExistingUser(regno, password);
			
			apiResponse.setStatus(dbResponse.getStatus());
			apiResponse.setMessage(dbResponse.getMessage());
			apiResponse.setResponse(dbResponse.getResponse());
		}
		
		
		return apiResponse;
	}
}
