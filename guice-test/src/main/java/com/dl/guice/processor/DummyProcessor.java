package com.dl.guice.processor;

public class DummyProcessor implements IProcessor{

	@Override
	public void process() {
		System.out.println("dummy process....");
	}

}
