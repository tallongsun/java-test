package com.dl.guice;

import com.dl.guice.processor.DummyProcessor;
import com.dl.guice.processor.IProcessor;
import com.google.inject.AbstractModule;

public class GuiceTestModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(IProcessor.class).to(DummyProcessor.class);
	}

}
