package com.dl.guice;

import com.dl.guice.processor.IProcessor;
import com.google.inject.Inject;

public class GuiceTestService {
	private IProcessor processor;
	
	@Inject
	public GuiceTestService(IProcessor processor){
		this.processor = processor;
	}
	
	public void service(){
		System.out.println("guice test service...");
		
		processor.process();

	}
}
