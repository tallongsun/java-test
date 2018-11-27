package com.dl.classloader;

import java.lang.reflect.Method;

public class TestService {
	
	private DescryptionClassLoader loader ;
	
	public TestService(DescryptionClassLoader loader) {
		this.loader = loader;
	}
	
	public void test() throws Exception{
		System.out.println(getLimit());
	}
	
	
	public String getLimit() throws Exception{
		ClassLoader parentLoader = Thread.currentThread().getContextClassLoader();

		Thread.currentThread().setContextClassLoader(loader);
		
		Class<?> clz = loader.loadClass("com.dl.lib.LimitUtil");
		Method method = clz.getMethod("test");
		String data = (String)method.invoke(null);
		
		Thread.currentThread().setContextClassLoader(parentLoader);
		
		return data;
	}
}
