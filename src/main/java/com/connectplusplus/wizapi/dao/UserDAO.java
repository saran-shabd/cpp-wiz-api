package com.connectplusplus.wizapi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;

import com.connectplusplus.wizapi.models.DBResponse;
import com.connectplusplus.wizapi.models.UserAccessToken;
import com.connectplusplus.wizapi.models.UserInfo;
import com.connectplusplus.wizapi.utils.CommonUtils;
import com.connectplusplus.wizapi.utils.EncryptionUtils;

/**
 * class containing utility methods related to DB operations on users
 * */

public class UserDAO {

	/**
	 * check if user is already registered with given registration number
	 * */
	public static DBResponse<Boolean> isUserAlreadyRegistered(String regno) {
		
		DBResponse<Boolean> dbResponse = new DBResponse<>();
		
		// SQL Queries
		String query1 = "SELECT COUNT(*) FROM users WHERE regno=?";
		
		try (Connection conn = CommonDAO.getConnection()) {
			
			PreparedStatement statement = conn.prepareStatement(query1);
			statement.setString(1, regno);
			ResultSet resultSet = statement.executeQuery();
			
			if (resultSet.next() && resultSet.getInt("count") == 0) {
				
				// user does not exists already
				
				dbResponse.setStatus(HttpStatus.OK);
				dbResponse.setMessage("User does not exists already");
				dbResponse.setResponse(false);
				
			} else {
				
				// user already exists
				
				dbResponse.setStatus(HttpStatus.BAD_REQUEST);
				dbResponse.setMessage("User already registered");
				dbResponse.setResponse(true);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			dbResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			dbResponse.setMessage("Internal Server Error");
			dbResponse.setStatus(null);
			
		}
		
		return dbResponse;
	}

	/**
	 * utility method to validate user access token in DB
	 * */
	public static DBResponse<Boolean> validateUserAccessToken(String regno, String token) {
		
		DBResponse<Boolean> dbResponse = new DBResponse<>();
		
		// SQL Queries
		String query1 = "SELECT COUNT(*) FROM users WHERE regno=? AND useraccesstoken=?";
		
		
		try (Connection conn = CommonDAO.getConnection()) {
			
			PreparedStatement statement = conn.prepareStatement(query1);
			statement.setString(1, regno);
			statement.setString(2, token);
			ResultSet resultSet = statement.executeQuery();
			
			if (resultSet.next() && resultSet.getInt("count") != 0) {
				
				// user access token verified
				
				dbResponse.setStatus(HttpStatus.OK);
				dbResponse.setMessage("User access token verified");
				dbResponse.setResponse(true);
				
			} else {
				
				// invalid user access token
				
				dbResponse.setStatus(HttpStatus.UNAUTHORIZED);
				dbResponse.setMessage("Invalid Arguments");
				dbResponse.setResponse(false);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			dbResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			dbResponse.setMessage("Internal Server Error");
			dbResponse.setResponse(null);
		}
		
		return dbResponse;
	}
	
	/**
	 * get basic information of a user
	 * */
	public static DBResponse<UserInfo> getUserInfo(String regno) {
		
		DBResponse<UserInfo> dbResponse = new DBResponse<>();
		
		// SQL Queries
		String query1 = "SELECT firstname, lastname, email, useraccesstoken FROM users WHERE regno=?";
		
		try (Connection conn = CommonDAO.getConnection()) {
			
			PreparedStatement statement = conn.prepareStatement(query1);
			statement.setString(1, regno);
			ResultSet resultSet = statement.executeQuery();
			
			if (resultSet.next()) {
				
				// user information fetched successfully from DB
								
				String firstname = resultSet.getString("firstname");
				String lastname = resultSet.getString("lastname");
				String email = resultSet.getString("email");
				String userAccessToken = resultSet.getString("useraccesstoken");
				
				UserInfo userInfo = new UserInfo(regno, firstname, lastname, email, userAccessToken);
				
				dbResponse.setStatus(HttpStatus.OK);
				dbResponse.setMessage("User Info");
				dbResponse.setResponse(userInfo);
			
			} else {
				
				// user is not registered
				
				dbResponse.setStatus(HttpStatus.BAD_REQUEST);
				dbResponse.setMessage("User not registered");
				dbResponse.setResponse(null);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			dbResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			dbResponse.setMessage("Internal Server Error");
			dbResponse.setResponse(null);
			
		}

		return dbResponse;
	}
	
	/**
	 * get complete information of a user, including all sensitive data
	 * */
	public static DBResponse<UserAccessToken> getFullUserInfo(String regno) {
		
		DBResponse<UserAccessToken> dbResponse = new DBResponse<>();
		
		// SQL Queries
		String query1 = "SELECT firstname, lastname, email, password, useraccesstoken, registration_time_stamp FROM users WHERE regno=?";
		
		
		try (Connection conn = CommonDAO.getConnection()) {
			
			// user information fetched successfully from DB
			
			PreparedStatement statement = conn.prepareStatement(query1);
			statement.setString(1, regno);
			ResultSet resultSet = statement.executeQuery();
			
			if (resultSet.next()) {
				
				String firstname = resultSet.getString("firstname");
				String lastname = resultSet.getString("lastname");
				String email = resultSet.getString("email");
				String password = resultSet.getString("password");
				String userAccessToken = resultSet.getString("useraccesstoken");
				Timestamp registrationTimeStamp = resultSet.getTimestamp("registration_time_stamp");
				
				UserAccessToken userInfo = new UserAccessToken(regno, firstname, lastname, email, userAccessToken, password, registrationTimeStamp);
				
				dbResponse.setStatus(HttpStatus.OK);
				dbResponse.setMessage("Full User Info");
				dbResponse.setResponse(userInfo);
				
			} else {
				
				// user is not registered
				
				dbResponse.setStatus(HttpStatus.BAD_REQUEST);
				dbResponse.setMessage("User is not registered");
				dbResponse.setResponse(null);
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
			
			dbResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			dbResponse.setMessage("Internal Server Error");
			dbResponse.setResponse(null);
		}
		
		return dbResponse;
	}

	/**
	 * register new user into DB
	 * */
	public static DBResponse<UserInfo> registerNewUser(String firstname, String lastname, String regno, String password) {
		
		DBResponse<UserInfo> dbResponse = new DBResponse<>();
		
		// SQL Queries
		String query1 = "INSERT INTO users(regno, firstname, lastname, password, email, useraccesstoken, registration_time_stamp) VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		
		// check if user with given registration number already exists
		
		DBResponse<Boolean> tempDBResponse = UserDAO.isUserAlreadyRegistered(regno);
		if (HttpStatus.OK != tempDBResponse.getStatus()) {
			
			// either user is already registered or something went wrong
			
			dbResponse.setStatus(tempDBResponse.getStatus());
			dbResponse.setMessage(tempDBResponse.getMessage());
			dbResponse.setResponse(null);
			
			return dbResponse;
		}
		
		
		// generate user access token
		
		String email = firstname.toLowerCase() + "." + regno + "@muj.manipal.edu";
		Timestamp currTimestamp = new Timestamp(System.currentTimeMillis());
		String token = CommonUtils.generateUserAccessToken(regno, firstname, lastname, email, password, currTimestamp);
		
		
		// store user in DB
		
		try (Connection conn = CommonDAO.getConnection()) {
			
			PreparedStatement statement = conn.prepareStatement(query1);
			statement.setString(1, regno);
			statement.setString(2, firstname);
			statement.setString(3, lastname);
			statement.setString(4, password);
			statement.setString(5, email);
			statement.setString(6, token);
			statement.setTimestamp(7, currTimestamp);
			
			statement.executeUpdate();
			
			
			// user registered successfully
			
			UserInfo userInfo = new UserInfo(regno, firstname, lastname, email, token);
			
			dbResponse.setStatus(HttpStatus.OK);
			dbResponse.setMessage("User registered successfully");
			dbResponse.setResponse(userInfo);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			dbResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			dbResponse.setMessage("Internal Server Error");
			dbResponse.setResponse(null);
			
		}
		
		return dbResponse;
	}

	/**
	 * utility method to check if user account is set to be deleted
	 * */
	public static DBResponse<Boolean> isUserAccountDeleted(String regno) {
		
		DBResponse<Boolean> dbResponse = new DBResponse<>();
		
		// SQL Queries
		String query1 = "SELECT request_date FROM delete_accounts WHERE regno=?";
		
		try (Connection conn = CommonDAO.getConnection()) {
			
			PreparedStatement statement = conn.prepareStatement(query1);
			statement.setString(1, regno);
			ResultSet resultSet = statement.executeQuery();
			
			dbResponse.setStatus(HttpStatus.OK);
			if (resultSet.next()) {
				dbResponse.setMessage("User account is to be deleted");
				dbResponse.setResponse(true);
			} else {
				dbResponse.setMessage("User account is activated");
				dbResponse.setResponse(false);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			dbResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			dbResponse.setMessage("Internal Server Error");
			dbResponse.setResponse(null);
		}
		
		return dbResponse;
	}

	/**
	 * re-activate user account, after user deleting the account
	 * */
	public static DBResponse<UserInfo> reactivateUserAccount(String regno, String password) {
		
		DBResponse<UserInfo> dbResponse = new DBResponse<>();
		
		// SQL Queries
		String query1 = "DELETE FROM delete_accounts WHERE regno=?";
		String query2 = "UPDATE users SET useraccesstoken=?, registration_time_stamp=? WHERE regno=?";
		
		
		try (Connection conn = CommonDAO.getConnection()) {
			
			conn.setAutoCommit(false);  // set auto-commit
			
			
			// delete user registration number from delete accounts table
			
			PreparedStatement statement = conn.prepareStatement(query1);
			statement.setString(1, regno);
			
			statement.executeUpdate();
			
			
			// generate user access token
			
			DBResponse<UserInfo> userDBResponse = UserDAO.getUserInfo(regno);
			
			if (HttpStatus.OK != userDBResponse.getStatus()) {
				
				// something went wrong
				
				dbResponse.setStatus(userDBResponse.getStatus());
				dbResponse.setMessage(userDBResponse.getMessage());
				dbResponse.setResponse(null);

			} else {
				
				Timestamp currTimestamp = new Timestamp(System.currentTimeMillis());
				String firstname = userDBResponse.getResponse().getFirstname();
				String lastname = userDBResponse.getResponse().getLastname();
				String email = userDBResponse.getResponse().getEmail();
				
				String token = CommonUtils.generateUserAccessToken(regno, firstname, lastname, email, password, currTimestamp);
				
				userDBResponse.getResponse().setUserAccessToken(token);
				
				
				// store current timestamp and user access token into DB
				
				statement = conn.prepareStatement(query2);
				statement.setString(1, token);
				statement.setTimestamp(2, currTimestamp);
				statement.setString(3, regno);
				
				statement.executeUpdate();
				
				conn.commit();  // commit all changes into DB
				
				
				dbResponse.setStatus(HttpStatus.OK);
				dbResponse.setMessage("User account re-activated");
				dbResponse.setResponse(userDBResponse.getResponse());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			dbResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			dbResponse.setMessage("Internal Server Error");
			dbResponse.setResponse(null);
		}
		
		
		return dbResponse;
	}

	/**
	 * login existing user
	 * */
	public static DBResponse<UserInfo> loginExistingUser(String regno, String password) {
		
		DBResponse<UserInfo> dbResponse = new DBResponse<>();
		
		// SQL Queries
		String query1 = "SELECT password FROM users WHERE regno=?";
		
		
		try (Connection conn = CommonDAO.getConnection()) {
			
			PreparedStatement statement = conn.prepareStatement(query1);
			statement.setString(1, regno);
			ResultSet resultSet = statement.executeQuery();
			
			
			// check password
			
			if (resultSet.next() && EncryptionUtils.compareHash(resultSet.getString("password"), password)) {
				
				// user credentials validated
				
				// get user information
				DBResponse<UserInfo> userDBResponse = UserDAO.getUserInfo(regno);
				
				if (HttpStatus.OK != userDBResponse.getStatus()) {
					
					// something went wrong
					
					dbResponse.setStatus(userDBResponse.getStatus());
					dbResponse.setMessage(userDBResponse.getMessage());
					dbResponse.setResponse(null);
					
				} else {
					
					// user logged in successfully
					
					dbResponse.setStatus(HttpStatus.OK);
					dbResponse.setMessage("User Logged In Successfully");
					dbResponse.setResponse(userDBResponse.getResponse());
				}
				
			} else {
				
				// either registration number or password is incorrect
				
				dbResponse.setStatus(HttpStatus.BAD_REQUEST);
				dbResponse.setMessage("Invalid Arguments");
				dbResponse.setResponse(null);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			dbResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			dbResponse.setMessage("Internal Server Error");
			dbResponse.setResponse(null);
			
		}
		
		return dbResponse;
	}

	/**
	 * delete existing user from DB
	 * */
	public static DBResponse<Boolean> deleteExistingUser(String regno) {
		
		DBResponse<Boolean> dbResponse = new DBResponse<>();
		
		// SQL Queries
		String query1 = "INSERT INTO delete_accounts(regno) VALUES (?)";
		String query2 = "UPDATE users SET useraccesstoken=?, registration_time_stamp=? WHERE regno=?";
		
		
		try (Connection conn = CommonDAO.getConnection()) {
			
			conn.setAutoCommit(false);  // disable auto-commit
			
			
			// insert user registration number into table for account deletion
			
			PreparedStatement statement = conn.prepareStatement(query1);
			statement.setString(1, regno);
			
			statement.executeUpdate();
			
			
			// delete user access token of the user
			
			statement = conn.prepareStatement(query2);
			statement.setNull(1, Types.VARCHAR);
			statement.setNull(2, Types.TIMESTAMP);
			statement.setString(3, regno);
			
			statement.executeUpdate();
			
			
			conn.commit();  // commit all changes to DB
			
			
			// user deleted successfully
			
			dbResponse.setStatus(HttpStatus.OK);
			dbResponse.setMessage("User deleted successfully");
			dbResponse.setResponse(true);
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			dbResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			dbResponse.setMessage("Internal Server Error");
			dbResponse.setResponse(false);
		}
		
		return dbResponse;
	}
	
	/**
	 * utility method to get registration number of all accounts that are due to be permanently deleted from DB
	 * */
	public static DBResponse<ArrayList<String>> getAccountsToBePermanentlyDeleted(String daysCount) {
		
		DBResponse<ArrayList<String>> dbResponse = new DBResponse<>();
		
		// SQL Queries
		String query1 = "SELECT regno FROM delete_accounts WHERE request_date >= (CURRENT_TIMESTAMP - INTERVAL \'" + daysCount + "\' day)";
		
		
		try (Connection conn = CommonDAO.getConnection()) {
			
			PreparedStatement statement = conn.prepareStatement(query1);
			ResultSet resultSet = statement.executeQuery();
			
			ArrayList<String> list = new ArrayList<>();
			
			while (resultSet.next()) {
				
				list.add(resultSet.getString("regno"));
			}
			
			dbResponse.setStatus(HttpStatus.OK);
			dbResponse.setMessage("Accounts to be permanently deleted from DB");
			dbResponse.setResponse(list);
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			dbResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			dbResponse.setMessage("Internal Server Error");
			dbResponse.setResponse(null);
		}
		
		
		return dbResponse;
	}
	
	public static DBResponse<Boolean> permanentlyDeleteUser(String regno) {
		
		DBResponse<Boolean> dbResponse = new DBResponse<>();
		
		// SQL Queries
		String query1 = "DELETE FROM users WHERE regno=?";
		String query2 = "DELETE FROM delete_accounts WHERE regno=?";
		
		
		try (Connection conn = CommonDAO.getConnection()) {
			
			conn.setAutoCommit(false);  // disable auto commit
			
			// delete user account
			
			PreparedStatement statement = conn.prepareStatement(query1);
			statement.setString(1, regno);
			
			statement.executeUpdate();
			
			
			// delete registration number from the table store accounts to be deleted permanently
			
			statement = conn.prepareStatement(query2);
			statement.setString(1, regno);
			
			statement.executeUpdate();
			
			conn.commit();  // commit all changes to DB
			
			
			// user account permanently deleted successfully
			
			dbResponse.setStatus(HttpStatus.OK);
			dbResponse.setMessage("User deleted permanently");
			dbResponse.setResponse(true);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			dbResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			dbResponse.setMessage("Internal Server Error");
			dbResponse.setResponse(false);
		}
		
		
		return dbResponse;
	}
}
