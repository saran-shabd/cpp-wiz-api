package com.connectplusplus.wizapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.connectplusplus.wizapi.workers.DeleteAccountsWorker;

@SpringBootApplication
public class Application {
	
	public static ApplicationContext factory;
	
	public static void startAllWorkerThreads() {
		
		new Thread(new DeleteAccountsWorker()).start();
	}

	public static void main(String[] args) {
		
		factory = SpringApplication.run(Application.class, args);
		
		// start all worker threads
		Application.startAllWorkerThreads();
	}
}
