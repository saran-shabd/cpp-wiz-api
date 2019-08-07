package com.connectplusplus.wizapi.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.connectplusplus.wizapi.Application;
import com.connectplusplus.wizapi.Properties;
import com.connectplusplus.wizapi.models.APIResponse;
import com.connectplusplus.wizapi.models.UserAccessToken;
import com.connectplusplus.wizapi.requestHandler.UserAuthRequestHandler;


/**
 * class containing common utility methods
 * */

@Component
public class CommonUtils {
	
	@Autowired
	private JavaMailSender mailSender;
	

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
	
	/**
	 * utility method to get UserAccessToken decoded from the token string value
	 * */
	public static APIResponse<UserAccessToken> getUserAccessTokenDecoded(String userAccessToken) {
		
		APIResponse<UserAccessToken> apiResponse = new APIResponse<>();
		
		// check if given user access token is valid
		
		APIResponse<Boolean> tempApiResponse = UserAuthRequestHandler.isUserAccessTokenValid(userAccessToken);
		if (tempApiResponse.getResponse()) {
			
			// decrypt & decode user access token
			UserAccessToken token = EncodingUtils.decodePojo(EncryptionUtils.decrypt(userAccessToken), UserAccessToken.class);
			
			apiResponse.setStatus(HttpStatus.OK);
			apiResponse.setMessage("UserAccessToken");
			apiResponse.setResponse(token);

		} else {
			
			// invalid user access token
			
			apiResponse.setStatus(tempApiResponse.getStatus());
			apiResponse.setMessage(tempApiResponse.getMessage());
			apiResponse.setResponse(null);
		}

		return apiResponse;
	}

	/**
	 * utility method to send emails to users.
	 * <br><br>
	 * returns <code>true</code> if email sent successfully else returns <code>false</code>.
	 * */
	public static boolean sendEmail(String receipientEmail, String subject, String emailType, HashMap<String, String> values) {
		
		try {
			MimeMessage message = Application.factory.getBean(CommonUtils.class).mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
			helper.setTo(receipientEmail);
			helper.setSubject(subject);
			
			// get email template
			
			String emailTemplate = CommonUtils.getEmailTemplate(emailType);
			if (null == emailTemplate) {
				// could not fetch email-template
				return false;
			}
			
			// fill email-template with real values
			for (String items : values.keySet()) {
				emailTemplate = emailTemplate.replace(items, values.get(items));
			}
			
			helper.setText(emailTemplate, true);  // 'true' for 'text/html' format
			
			// send email
			Application.factory.getBean(CommonUtils.class).mailSender.send(message);
			
		} catch (MessagingException e) {
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}


	/**
	 * utility method to get email-template from file system
	 * */
	public static String getEmailTemplate(String emailType) {
		
		String emailTemplatesRoute = Application.factory.getBean(Properties.class).getAppEmailTemplatesRoute();
		
		String templatePath = emailTemplatesRoute + emailType + ".html";
		String emailTemplate = null;
		try {
			
			/*
			 * TODO: Cannot get to resource/template/email directory. Relative paths in Spring Boot.
			 * */
			
			emailTemplate = new String(Files.readAllBytes(Paths.get(templatePath)));
			
		} catch (NoSuchFileException e) {
			e.printStackTrace();
			
			emailTemplate = null;
			
		} catch (IOException e) {
			e.printStackTrace();
			
			emailTemplate = null;
		}

		return emailTemplate;
	}
}
