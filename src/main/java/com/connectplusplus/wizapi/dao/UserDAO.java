package com.connectplusplus.wizapi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

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
				dbResponse.setMessage("User already exists");
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
	 * login existing user
	 * */
	public static DBResponse<UserInfo> loginExistingUser(String regno, String password) {
		
		DBResponse<UserInfo> dbResponse = new DBResponse<>();
		
		// SQL Queries
		String query1 = "SELECT firstname, lastname, email, useraccesstoken, password FROM users WHERE regno=?";
		
		
		try (Connection conn = CommonDAO.getConnection()) {
			
			PreparedStatement statement = conn.prepareStatement(query1);
			statement.setString(1, regno);
			ResultSet resultSet = statement.executeQuery();
			
			if (resultSet.next() && EncryptionUtils.compareHash(resultSet.getString("password"), password)) {
				
				// user credentials validated
				
				String firstname = resultSet.getString("firstname");
				String lastname = resultSet.getString("lastname");
				String email = resultSet.getString("email");
				String userAccessToken = resultSet.getString("useraccesstoken");
				
				UserInfo userInfo = new UserInfo(regno, firstname, lastname, email, userAccessToken);
				
				dbResponse.setStatus(HttpStatus.OK);
				dbResponse.setMessage("User Logged In Successfully");
				dbResponse.setResponse(userInfo);
				
			} else {
				
				// invalid user credentials
				
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
		String query1 = "DELECT FROM users WHERE regno=?";
		
		
		try (Connection conn = CommonDAO.getConnection()) {
			
			PreparedStatement statement = conn.prepareStatement(query1);
			statement.setString(1, regno);
			
			statement.executeUpdate();
			
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
}
