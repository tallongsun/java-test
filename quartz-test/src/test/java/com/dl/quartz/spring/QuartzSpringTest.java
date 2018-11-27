package com.dl.quartz.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath:/datasource.xml", "classpath:/quartz.xml" })
public class QuartzSpringTest {
	
	public static void main(String[] args) {
		SpringApplication.run(QuartzSpringTest.class, args);
	}
}
