package com.dl.spring.mvc.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class TestService {
	public String testSessionPass(){
		HttpServletRequest httpRequest = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		return "hello,"+httpRequest.getSession().getAttribute("test");
	}
}
