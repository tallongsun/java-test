package com.dl.resource;

public class TestResource {
	public static void main(String[] args) throws Exception{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		System.out.println(loader.getResource("a.txt"));
		System.out.println(loader.getResource("a.txt").toURI());
		
		System.out.println(loader.getResource("a/b.txt"));
		
		System.out.println(loader.getResource("com/dl/resource/TestResource.class"));
		
	}
}
