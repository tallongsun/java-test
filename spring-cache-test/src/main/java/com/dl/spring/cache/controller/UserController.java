package com.dl.spring.cache.controller;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	
	@RequestMapping("/")
	public String testNoSession(){
		//这种情况不创建session
		return "test";
	}

	@RequestMapping("/test")
	public String test(HttpSession session){
		//这种情况即使不对session操作，也会创建session，相当于调用过httpServletRequest.getHttpSession就会创建
		//spring-session的session对应的cookie-key为"session"
		session.setAttribute("testkey", "testval");
		return (String)session.getAttribute("testkey");
	}
}
