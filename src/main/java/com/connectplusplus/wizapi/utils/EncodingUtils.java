package com.connectplusplus.wizapi.utils;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.connectplusplus.wizapi.Application;
import com.connectplusplus.wizapi.Properties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


/**
 * class containing utility methods related to encoding operations
 * */

public class EncodingUtils {
	
	private static final Properties properties;
	
	static {
		// Initializations
		properties = Application.factory.getBean(Properties.class);
	}

	/**
	 * utility method to encode string value of a JSON object
	 * */
	public static String encode(String subject) {
		
		return Jwts.builder()
				.setSubject(subject)
				.signWith(
					new SecretKeySpec(
						DatatypeConverter.parseBase64Binary(properties.getAppSecret()),
						SignatureAlgorithm.HS256.getJcaName()
					)
				)
				.compact();
	}
	
	/**
	 * utility method to decode string value of a JSON object
	 * */
	public static String decode(String jwt) {
		
		return Jwts.parser().
				setSigningKey(DatatypeConverter.parseBase64Binary(properties.getAppSecret()))
				.parse(jwt)
				.getBody()
				.toString();
	}
	
	/**
	 * utility method to encode a POJO
	 * */
	public static <Type> String encodePojo(Type pojo) {
		
		String json = JsonUtils.convertPojoToJson(pojo);
		
		String token = encode(json);
		
		return token;
	}
	
	/**
	 * utility method to decode a token into its corresponding POJO
	 * */
	public static <Type> Type decodePojo(String jwt, Class<Type> typeClass) {
		
		String json = decode(jwt);
		json = json.substring(5, json.length() - 1);
				
		Type pojo = JsonUtils.convertJsonToPojo(json, typeClass);
				
		return pojo;
	}
}
