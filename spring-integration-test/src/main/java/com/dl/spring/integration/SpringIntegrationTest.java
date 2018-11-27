package com.dl.spring.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

import com.dl.spring.integration.service.HelloApi;

@SpringBootApplication
//@ImportResource({ "classpath:application-channel.xml" })
//@ImportResource({ "classpath:application-simple-gateway.xml" })
//@ImportResource({ "classpath:application-mq.xml" })
@ImportResource({ "classpath:application-mq2.xml" })
public class SpringIntegrationTest {
	public static void main(String[] args) {
		
		ConfigurableApplicationContext ctx = SpringApplication.run(SpringIntegrationTest.class, args);

//		testChannel(ctx);
//		testSimpleGateway(ctx);
//		testMQ(ctx);
		testMQ2(ctx);
		
		ctx.close();
	}

	//SpringIntegrationTest->Channel->service-activator->HelloService->Channel->SpringIntegrationTest
	static void testChannel(ConfigurableApplicationContext ctx) {
		MessageChannel inputChannel = ctx.getBean("inputChannel", MessageChannel.class);
		PollableChannel outputChannel = ctx.getBean("outputChannel", PollableChannel.class);
		//向inputchannel发送消息，无法直接获取返回值
		inputChannel.send(new GenericMessage<String>("World"));
		//通过outputchannel获取返回值
		System.out.println("==> HelloWorldDemo: " + outputChannel.receive(0).getPayload());
	}

	//SpringIntegrationTest->HelloApi(GateWay)->Channel->service-activator->HelloService->SpringIntegrationTest
	static void testSimpleGateway(ConfigurableApplicationContext ctx) {
		HelloApi helloApi = ctx.getBean("simpleGateway", HelloApi.class);
		//通过网关可以直接获取返回值了
		System.out.println("==> HelloWorldDemo: "+ helloApi.sayHello("World"));
	}

	//SpringIntegrationTest->TopicChannel->outbound-channel-adapter->ActiveMQ
	//  ->message-driven-channel-adapter->ListenerChannel->service-activator->MessageListener
	static void testMQ(ConfigurableApplicationContext ctx) {
		MessageChannel inputChannel = ctx.getBean("topicChannel",  MessageChannel.class);
		inputChannel.send(MessageBuilder.withPayload("PayLoad for 1").build());
	}
	
	//SpringIntegrationTest->Channel->outbound-channel-adapter->ActiveMQ->message-driven-channel-adapter->Channel->service-activator->RetrievePayloadService->Channel->MessageListener
	static void testMQ2(ConfigurableApplicationContext ctx) {
		MessageChannel inputChannel = ctx.getBean("topicChannel",  MessageChannel.class);
		inputChannel.send(MessageBuilder.withPayload("1").build());
	}
}
