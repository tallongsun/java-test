package com.dl.classloader;

public class TestMain {
	
	public static void main(String[] args) throws Exception{

		DescryptionClassLoader loader = new DescryptionClassLoader();
		
		TestService service = new TestService(loader);
		service.test();
		

	}
	
}
