package com.dl.elasticjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations={"classpath:/applicationContext.xml"})
public class ElasticJobTest {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ElasticJobTest.class, args);
		System.out.println(context);
	}

}
