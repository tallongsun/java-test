package com.dl.spring.aop.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


public class WelcomeInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("welcome from interceptor...");  
        Object ret=invocation.proceed();  
        return ret; 
	}


}
