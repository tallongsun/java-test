package com.dl.dubbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import com.dl.dubbo.service.XxxService;
/**
 *	SpringBoot方式调用服务 
 *
 */
@EnableAutoConfiguration
@ComponentScan
@ImportResource({"classpath:service.xml"})
public class SpringBootTest {

	public static void main(String[] args) {
		
		ApplicationContext ctx = SpringApplication.run(SpringBootTest.class, args);
		
		XxxService service = ctx.getBean(XxxService.class);
		System.out.println(service.xxx());
		
	}

}
