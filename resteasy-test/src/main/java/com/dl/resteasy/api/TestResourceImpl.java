package com.dl.resteasy.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dl.resteasy.service.TestService;

@Controller
public class TestResourceImpl implements TestResource{
	@Autowired
	private TestService testService;
	
	@Override
	public String empty() {
		return "";
	}

	@Override
	public String smallStr() {
		return testService.generateStr(1);
	}

	@Override
	public String mediumStr() {
		return testService.generateStr(10);
	}

	@Override
	public String largeStr() {
		return testService.generateStr(100);
	}

}
