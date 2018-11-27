package com.dl.spring.aop.aspectj.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect //1.declare an aspect
public class ProfilingAspect {
	
	@Pointcut("execution(* com.dl.spring.aop.aspectj.service..*.*(..))")//2.declare a pointcut
	private void businessService() {
		System.out.println("businessService");
	}
	
	
	@Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
	private void transactional() {
		System.out.println("transactional");
	}
	
	@Around(" businessService() && transactional()") //3. declare an advice
	private Object transactionalBusinessService(ProceedingJoinPoint jointPoint) throws Throwable {
		System.out.println("transactional & businessService ");
		
		StopWatch sw = new StopWatch(this.getClass().getSimpleName());
		try{
			sw.start(jointPoint.getSignature().getName());
			return jointPoint.proceed();
		}finally {
			sw.stop();
			System.out.println(sw.prettyPrint());
		}
	}
}
