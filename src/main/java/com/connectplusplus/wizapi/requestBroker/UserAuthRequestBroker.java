package com.connectplusplus.wizapi.requestBroker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.connectplusplus.wizapi.models.APIResponse;
import com.connectplusplus.wizapi.models.UserInfo;
import com.connectplusplus.wizapi.requestHandler.UserAuthRequestHandler;

@RestController
@RequestMapping("/user/auth/")
public class UserAuthRequestBroker {

	@RequestMapping(
				path = "register",
				method = RequestMethod.POST
			)
	public ResponseEntity<APIResponse<UserInfo>> registerNewUser(
				@RequestHeader String firstname, 
				@RequestHeader String lastname, 
				@RequestHeader String regno, 
				@RequestHeader String password
			) {
		
		APIResponse<UserInfo> apiResponse = null;
		
		try {
			apiResponse = UserAuthRequestHandler.registerNewUser(firstname, lastname, regno, password);
		} catch (Exception e) {
			e.printStackTrace();
			apiResponse = new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", null);
		}
		
		return new ResponseEntity<>(apiResponse, apiResponse.getStatus());
	}

	@RequestMapping(
				path = "login",
				method = RequestMethod.POST
			)
	public ResponseEntity<APIResponse<UserInfo>> loginUser(
				@RequestHeader String regno,
				@RequestHeader String password
			) {
		
		APIResponse<UserInfo> apiResponse = null;
		
		try {
			apiResponse = UserAuthRequestHandler.loginUser(regno, password);
		} catch (Exception e) {
			e.printStackTrace();
			apiResponse = new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", null);
		}
		
		return new ResponseEntity<>(apiResponse, apiResponse.getStatus());				
	}

	@RequestMapping(
				path = "validate",
				method = RequestMethod.POST
			)
	public ResponseEntity<APIResponse<Boolean>> validateUserAccessToken(@RequestHeader("useraccesstoken") String userAccessToken) {
		
		APIResponse<Boolean> apiResponse = null;

		try {
			apiResponse = UserAuthRequestHandler.isUserAccessTokenValid(userAccessToken);
		} catch (Exception e) {
			e.printStackTrace();
			apiResponse = new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", null);
		}
		
		return new ResponseEntity<>(apiResponse, apiResponse.getStatus());
	}
}
