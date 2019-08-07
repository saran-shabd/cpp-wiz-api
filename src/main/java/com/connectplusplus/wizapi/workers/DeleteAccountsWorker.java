package com.connectplusplus.wizapi.workers;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.http.HttpStatus;

import com.connectplusplus.wizapi.EmailType;
import com.connectplusplus.wizapi.dao.UserDAO;
import com.connectplusplus.wizapi.models.DBResponse;
import com.connectplusplus.wizapi.models.UserInfo;
import com.connectplusplus.wizapi.utils.CommonUtils;

/**
 * this worker thread runs once everyday to check if there are any accounts to be deleted.
 * <br><br>
 * an email is sent to the user after the account has been deleted permanently
 * */

public class DeleteAccountsWorker extends Thread {

	private static final long initialWaitTime = 60 * 1000L;  // 60 seconds
	private static final long sleepTime = 24 * 60 * 60 * 1000L;  // 24 hours
	private static final String daysCount = "15";  // number of days to store deleted accounts in DB
	
	@Override
	public void run() {
		
		// set thread properties
		
		this.setName("DeleteAccountsWorker");
		this.setPriority(MIN_PRIORITY);
		
		
		// wait for sometime for the server to complete its setup process
		
		try {
			Thread.sleep(initialWaitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		while (true) {
			
			// get registration number of all accounts that are due to be deleted permanently from DB
			
			DBResponse<ArrayList<String>> listOfAccountsToBeDeleted = UserDAO.getAccountsToBePermanentlyDeleted(daysCount);

			if (HttpStatus.OK == listOfAccountsToBeDeleted.getStatus()) {
				
				for (String regno : listOfAccountsToBeDeleted.getResponse()) {
					
					// get user information
					DBResponse<UserInfo> userInfo = UserDAO.getUserInfo(regno);
					
					
					// delete user account
					
					DBResponse<Boolean> isUserDeleted = UserDAO.permanentlyDeleteUser(regno);
					
					if (isUserDeleted.getResponse()) {
						
						// send notification email to the user about account being permanently deleted
						
						String email = userInfo.getResponse().getEmail();
						String fullname = userInfo.getResponse().getFirstname() + " " + userInfo.getResponse().getLastname();
						String subject = "Account Deleted Permanently";
						
						HashMap<String, String> values = new HashMap<>();
						values.put("@@regno@@", regno);
						values.put("@@name@@", fullname);
						
						boolean isEmailSent = CommonUtils.sendEmail(email, subject, EmailType.PERMANENT_DELETE_ACCOUNT, values);
						
						if (!isEmailSent) {
							
							// email could not be sent
							
							// TODO: Do something here, because the account has already been deleted permanently
						}
					}
				}
			}
			
			
			// sleep for 24 hours
			
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
