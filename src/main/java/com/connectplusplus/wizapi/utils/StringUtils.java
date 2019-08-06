package com.connectplusplus.wizapi.utils;


/**
 * class containing utility methods for string operations
 * */

public class StringUtils {
	
	/**
	 * utility method to check if a given array of strings contains any empty string
	 * */
	public static boolean containsAnyEmptyString(String[] arr) {
		
		for (String item : arr) {
			if (null == item || "".equals(item))
				return true;
		}
		
		return false;
	}
	
	/**
	 * utility method to check if a string is empty
	 * */
	public static boolean isEmpty(String text) {
		
		return (null == text || "".equals(text));
	}
	
	/**
	 * utility method to check if a given string contains all numeric characters
	 * */
	public static boolean containsAllNumericChars(String text) {
		
		return text.matches("^\\d*$");
	}
	
	/**
	 * utility method to check if registration number is correctly formatted 
	 * */
	public static boolean isValidRegno(String regno) {
		
		// check if registration number is all numeric
		if (!containsAllNumericChars(regno))
			return false;
		
		// check if registration number has 9 digits
		if (9 != regno.length())
			return false;
		
		return true;
	}
	
	/**
	 * utility method to check if a given password is strong enough 
	 * <br><br>
	 * <b>Password Rules:</b>
	 * <ul>
	 * 	<li>At least 1 Digit</li>
	 * 	<li>At least 1 Lower Case Letter</li>
	 * 	<li>At least 1 Upper Case Letter</li>
	 * 	<li>At least 1 Special Character</li>
	 * 	<li>No whitespace allowed</li>
	 * 	<li>Minimum Length must be 8 Characters</li>
	 * </ul>
	 * */
	public static boolean isStrongPassword(String password) {
		
		return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
	}
}
