package com.dl.spring.aop.service;

import org.springframework.stereotype.Service;

@Service
public class DummyServiceImpl implements DummyService {

	@Override
	public void doSomething() {
		System.out.println("do something...");
	}

}
