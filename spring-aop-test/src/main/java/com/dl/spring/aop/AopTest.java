package com.dl.spring.aop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

import com.dl.spring.aop.service.DummyService;

@SpringBootApplication
@ImportResource({"classpath:context.xml"})
public class AopTest {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(AopTest.class, args);
		DummyService ds = ctx.getBean(DummyService.class);
		ds.doSomething();
	}

}
