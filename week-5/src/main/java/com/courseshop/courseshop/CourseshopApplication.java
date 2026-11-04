package com.courseshop.courseshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//disable sercurity
// @SpringBootApplication(
// 	exclude = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
@SpringBootApplication 
public class CourseshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseshopApplication.class, args);
	}

}
