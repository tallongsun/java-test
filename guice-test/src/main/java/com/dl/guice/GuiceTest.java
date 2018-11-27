package com.dl.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceTest {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new GuiceTestModule());
		
		GuiceTestService serivce = injector.getInstance(GuiceTestService.class);
		serivce.service();
	}

}
