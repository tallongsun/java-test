
package com.dl.ssoclient;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
		org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
})
public class SSOauth2ClientApplication {

	public static void main(String[] args) {
		System.setProperty("logging.config", System.getProperty("APP_HOME") + File.separator + "conf" + File.separator + "logback.xml");
		
		SpringApplication.run(SSOauth2ClientApplication.class, args);

	}

}
