package com.dl.resteasy;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/api/v1/")
public class SampleJaxrsApplication extends Application{
	
	@Bean
	public ValidationExceptionHandler validationExceptionHandler(){
		return new ValidationExceptionHandler();
	}
}
