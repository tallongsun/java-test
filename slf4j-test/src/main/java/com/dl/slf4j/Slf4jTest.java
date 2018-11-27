package com.dl.slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jTest {

	static Logger logger = LoggerFactory.getLogger(Slf4jTest.class);
	
	public static void main(String[] args) throws Exception{
	    
	    logger.info("Hello World {}.",1,2);
	    
	    Thread.sleep(10000);
	    
	    logger.info("Hello World {}.",1,2);
	    
	    System.out.println("over");
	    
	}

}
