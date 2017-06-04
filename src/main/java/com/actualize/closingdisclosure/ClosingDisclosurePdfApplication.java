package com.actualize.closingdisclosure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

/**
 * This class initiates the current application 
 * @author sboragala
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = "com.actualize.closingdisclosure")
@ImportResource("classpath:config.xml")
public class ClosingDisclosurePdfApplication extends SpringBootServletInitializer {
	
	private static final Logger LOG = LogManager.getLogger(ClosingDisclosurePdfApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(ClosingDisclosurePdfApplication.class, args);
	}
}
