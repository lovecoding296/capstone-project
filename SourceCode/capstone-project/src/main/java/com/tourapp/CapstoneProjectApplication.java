package com.tourapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class CapstoneProjectApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(CapstoneProjectApplication.class);

	public static void main(String[] args) {
		logger.info("Running main...");
		SpringApplication.run(CapstoneProjectApplication.class, args);
	}

}
