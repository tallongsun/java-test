package com.dl.tingyun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class TingyunTest {
	//VM args : -javaagent:/Users/tallong/JavaProjects/test/tingyun-test/tingyun/tingyun-agent-java.jar
	public static void main(String[] args) {
		SpringApplication.run(TingyunTest.class, args);
	}
	
	@RequestMapping("/")
	public String hello(){
		return "hello";
	}
	
}
