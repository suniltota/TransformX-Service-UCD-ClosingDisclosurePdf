package com.actualize.closingdisclosure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(scanBasePackages = "com.actualize.closingdisclosure")
@ImportResource("classpath:config.xml")
public class ClosingDisclosurePdfApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClosingDisclosurePdfApplication.class, args);
	}
}
