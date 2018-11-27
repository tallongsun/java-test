package com.dl.dubbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import com.dl.dubbo.service.XxxService;

/**
 * 
 * Dubbo代码方式调用服务
 *
 */
@EnableAutoConfiguration
@ComponentScan
@ImportResource({"classpath:dubbo-consumer.xml"})
public class DubboConsumerTest {
	/**
	 * -Dserver.port=8081 
	 */
	public static void main(String[] args) {
		
		ApplicationContext ctx = SpringApplication.run(DubboConsumerTest.class, args);
		XxxService xxxService = ctx.getBean(XxxService.class);
		System.out.println(xxxService.xxx());

	}

}
