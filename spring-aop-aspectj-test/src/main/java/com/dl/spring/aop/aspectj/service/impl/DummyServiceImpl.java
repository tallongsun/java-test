package com.dl.spring.aop.aspectj.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dl.spring.aop.aspectj.service.DummyService;

@Service
public class DummyServiceImpl implements DummyService{

	@Override
	@Transactional
	public void doSomething() {
		System.out.println("do sth...");
	}

}
