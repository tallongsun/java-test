package com.dl.jni;

//cd /Users/tallong/JavaProjects/test/java6-test/src/main/java
//javah com.dl.jni.JniTest 
//g++ -I/Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk/Contents/Home/include -I/Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk/Contents/Home/include/darwin/  -c com_dl_jni_JniTest.cpp
//g++ -dynamiclib -o libJniTest.dylib com_dl_jni_JniTest.o
//cd /Users/tallong/JavaProjects/test/java6-test/target/classes
//java com.dl.jni.JniTest

public class JniTest {
	static {
		System.out.println(System.getProperty("java.library.path"));  
		System.loadLibrary("JniTest");
	}

	public native void hello(String str);
	
	public static void main(String[] args) {
		
		new JniTest().hello("sx");
	}
}
