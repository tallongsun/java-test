package com.dl.agent;

public class AgentTestMain {
	//-javaagent:javaagent-test-0.0.1-SNAPSHOT-jar-with-dependencies.jar="xxx"
	public static void main(String[] args) throws Exception{
		AgentTest at = new AgentTest();
		at.say();
		
	}
}
