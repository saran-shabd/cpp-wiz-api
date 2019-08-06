package com.connectplusplus.wizapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {
	
	public static ApplicationContext factory;

	public static void main(String[] args) {
		factory = SpringApplication.run(Application.class, args);
	}
}
