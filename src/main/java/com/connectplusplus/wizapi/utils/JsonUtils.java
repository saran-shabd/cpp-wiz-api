package com.connectplusplus.wizapi.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * class containing utility methods related to JSON-POJO operations
 * */

public class JsonUtils {

	private static final ObjectMapper mapper;
	
	static {
		// Initializations
		mapper = new ObjectMapper();
	}
	
	/**
	 * utility method to convert a POJO into a JSON valued string
	 * */
	public static <Type> String convertPojoToJson(Type pojo) {
		
		String json = null;
		try {
			json = mapper.writeValueAsString(pojo);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * utility method to convert a JSON valued string into its corresponding POJO
	 * */
	public static <Type> Type convertJsonToPojo(String json, Class<Type> typeClass) {
	
		Type pojo = null;
		try {
			pojo = mapper.readValue(json, typeClass);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return pojo;
	}
}
