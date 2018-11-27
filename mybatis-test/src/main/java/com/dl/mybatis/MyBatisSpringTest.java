package com.dl.mybatis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.dl.mybatis.service.BlogService;
import com.dl.mybatis.service.FooService;

@SpringBootApplication
@PropertySources({ @PropertySource(value = "classpath:datasource-spring.properties")})
@ImportResource(value = { "classpath:datasource-spring.xml"})
public class MyBatisSpringTest {

	
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(MyBatisSpringTest.class, args);
		
//		FooService fooService = ctx.getBean(FooService.class);
//		System.out.println(fooService.doSomeBusinessStuff("1"));
		
		testBlog(ctx);
	}

	private static void testBlog(ApplicationContext ctx) {
		BlogService blogService = ctx.getBean(BlogService.class);
		blogService.test();
	}

}
