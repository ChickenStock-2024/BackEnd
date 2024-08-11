package com.sascom.chickenstock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class ChickenstockApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChickenstockApplication.class, args);
	}
}
