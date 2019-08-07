package com.connectplusplus.wizapi.requestHandler;

import java.util.HashMap;

import org.springframework.http.HttpStatus;

import com.connectplusplus.wizapi.EmailType;
import com.connectplusplus.wizapi.dao.UserDAO;
import com.connectplusplus.wizapi.models.APIResponse;
import com.connectplusplus.wizapi.models.DBResponse;
import com.connectplusplus.wizapi.models.UserAccessToken;
import com.connectplusplus.wizapi.models.UserInfo;
import com.connectplusplus.wizapi.utils.CommonUtils;
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
			
			// check if user account has been deleted
			
			DBResponse<Boolean> isAccountDeleted = UserDAO.isUserAccountDeleted(regno);
			if (HttpStatus.OK != isAccountDeleted.getStatus()) {
				
				// something went wrong
				
				apiResponse.setStatus(isAccountDeleted.getStatus());
				apiResponse.setMessage(isAccountDeleted.getMessage());
				apiResponse.setResponse(null);
				
				return apiResponse;
			}
			
			if (isAccountDeleted.getResponse()) {
				
				// reactivate user account & login user
				
				DBResponse<UserInfo> dbResponse = UserDAO.reactivateUserAccount(regno, password);
				
				apiResponse.setStatus(dbResponse.getStatus());
				apiResponse.setMessage(dbResponse.getMessage());
				apiResponse.setResponse(dbResponse.getResponse());
				
				
				if (HttpStatus.OK == apiResponse.getStatus()) {
					
					// send notification email to the user about reactivation of account
					
					String fullname = dbResponse.getResponse().getFirstname() + " " + dbResponse.getResponse().getLastname();
					String email = dbResponse.getResponse().getEmail();
					String subject = "Account Reactivated Successfully";
					
					HashMap<String, String> values = new HashMap<>();
					values.put("@@regno@@", dbResponse.getResponse().getRegno());
					values.put("@@name@@", fullname);
					
					boolean isEmailSent = CommonUtils.sendEmail(email, subject, EmailType.REACTIVATE_ACCOUNT, values);
					if (!isEmailSent) {
						
						// email could not be sent
						
						// TODO: Do something here, because the account has already been reactivated
					}
				}
				
			} else {
				
				// login user
				
				DBResponse<UserInfo> dbResponse = UserDAO.loginExistingUser(regno, password);
				
				apiResponse.setStatus(dbResponse.getStatus());
				apiResponse.setMessage(dbResponse.getMessage());
				apiResponse.setResponse(dbResponse.getResponse());
			}
		}
		
		
		return apiResponse;
	}

	public static APIResponse<Boolean> deleteUser(String userAccessToken) {
		
		APIResponse<Boolean> apiResponse = new APIResponse<>();
		
		
		// decode user access token
		
		APIResponse<UserAccessToken> tempApiResponse = CommonUtils.getUserAccessTokenDecoded(userAccessToken);
		if (null == tempApiResponse.getResponse()) {
			
			// user access token could be decoded
			
			apiResponse.setStatus(tempApiResponse.getStatus());
			apiResponse.setMessage(tempApiResponse.getMessage());
			apiResponse.setResponse(null);
			
			return apiResponse;
		}
		
		UserAccessToken token = tempApiResponse.getResponse();
		
		
		// delete user from DB
		
		DBResponse<Boolean> dbResponse = UserDAO.deleteExistingUser(token.getRegno());
		if (!dbResponse.getResponse()) {
			
			// account could not be deleted
			
			apiResponse.setStatus(dbResponse.getStatus());
			apiResponse.setMessage(dbResponse.getMessage());
			apiResponse.setResponse(false);
			
			return apiResponse;
		}
		
		
		// send account delete confirmation email
		
		String fullname = token.getFirstname() + " " + token.getLastname();
		
		HashMap<String, String> values = new HashMap<>();
		values.put("@@regno@@", token.getRegno());
		values.put("@@name@@", fullname);
		
		boolean emailSent = CommonUtils.sendEmail(token.getEmail(), "Account Deleted Successfully", EmailType.DELETE_ACCOUNT, values);
		if (!emailSent) {
			
			// email could not be sent
			
			// TODO: Do something here, because the account has already been deleted
		}
		
		apiResponse.setStatus(HttpStatus.OK);
		apiResponse.setMessage("Account Deleted Successfully");
		apiResponse.setResponse(true);
		
		
		return apiResponse;
	}
}
