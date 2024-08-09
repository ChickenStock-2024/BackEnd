package com.sascom.chickenstock;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

@ConfigurationPropertiesScan
@SpringBootApplication
public class ChickenstockApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChickenstockApplication.class, args);
	}


	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("*")
						.allowedMethods("GET", "POST", "PUT", "DELETE")
						.allowedHeaders("Authorization", "Content-Type");
			}
		};
	}

	@PostConstruct
	public void init() {
		String uploadDirectory = File.separator + "resource";
		File directory = new File(uploadDirectory);
		if (!directory.exists()) {
			directory.mkdirs(); // 디렉터리 생성
		}
	}
}
