package com.connectplusplus.wizapi.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.connectplusplus.wizapi.Application;
import com.connectplusplus.wizapi.Properties;


/**
 * class containing common utility methods related to DB operations
 * */

public class CommonDAO {

	private static final Properties properties;
	
	static {
		// Initializations
		properties = Application.factory.getBean(Properties.class);
	}
	
	/**
	 * utility method to get database connection
	 * */
	public static Connection getConnection() throws SQLException {
		
		Connection conn = DriverManager.getConnection(
				// DB URL Credentials
				"jdbc:" + 
				properties.getDbService() + "://" + 
				properties.getDbHost() + ":" + 
				properties.getDbPort() + "/" + 
				properties.getDbName(),
				
				// DB User Credentials
				properties.getDbUser(), 
				properties.getDbPassword()
		);
		
		return conn;
	}
}
