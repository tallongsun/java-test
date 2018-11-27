package com.dl.spring.integration.service;

public class HelloService implements HelloApi{
	public String sayHello(String name) {
		return "Hello " + name + " in service";
	}
}