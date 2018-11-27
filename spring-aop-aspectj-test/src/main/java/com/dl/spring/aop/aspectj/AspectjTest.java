package com.dl.spring.aop.aspectj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.dl.spring.aop.aspectj.service.DummyService;


@SpringBootApplication
@EnableAspectJAutoProxy//0.enable @AspectJ support
public class AspectjTest {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(AspectjTest.class, args);
		DummyService ds = ctx.getBean(DummyService.class);
		ds.doSomething();
	}

}
