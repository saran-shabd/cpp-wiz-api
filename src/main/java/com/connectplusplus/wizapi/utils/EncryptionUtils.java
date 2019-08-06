package com.connectplusplus.wizapi.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.util.DigestUtils;

import com.connectplusplus.wizapi.Application;
import com.connectplusplus.wizapi.Properties;


/**
 * class containing utility methods for encryption operations
 * */

public class EncryptionUtils {
	
	private static final Properties properties;
	
	static {
		// Initializations
		properties = Application.factory.getBean(Properties.class);
	}
	
	/**
	 * utility method to encrypt a string.
	 * the encrypted string can be decrypted back to the plain text.
	 * */
	public static String encrypt(String plainText) {
		
		try {
			IvParameterSpec iv = new IvParameterSpec(properties.getEncryptionIv().getBytes("UTF-8"));
			SecretKeySpec secretKey = new SecretKeySpec(properties.getEncryptionKey().getBytes("UTF-8"), "AES");
		
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			
			byte[] encrypted = cipher.doFinal(plainText.getBytes());
			
			return Base64.encodeBase64String(encrypted);
			
		} catch (Exception e) {}
	
		return null;
	}
	
	/**
	 * decrypt a string back to plain text
	 * */
	public static String decrypt(String encryptedText) {
		
		try {
			IvParameterSpec iv = new IvParameterSpec(properties.getEncryptionIv().getBytes("UTF-8"));
			SecretKeySpec secretKey = new SecretKeySpec(properties.getEncryptionKey().getBytes("UTF-8"), "AES");
		
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
			
			byte[] plainText = cipher.doFinal(Base64.decodeBase64(encryptedText));
			
			return new String(plainText);
			
		} catch (Exception e) {}
		
		return null;
	}
	
	/**
	 * hash a plain-text.
	 * plain-text cannot be obtained from the hashed string again.
	 * */
	public static String getHash(String plainText) {
	
		return DigestUtils.md5DigestAsHex(plainText.getBytes());
	}
	
	/**
	 * compare a hash with a plain-text for equality
	 * */
	public static boolean compareHash(String hash, String plainText) {
		
		return hash.equals(EncryptionUtils.getHash(plainText));
	}
}
