package com.dl.kafka.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.Assert;

@SpringBootApplication
public class KafkaTest {
    private static KafkaTemplate<Integer, String> kafkaTemplate;
    

	public static void main(String... args) {
        ApplicationContext context = SpringApplication.run(KafkaTest.class, args);
        
        Assert.notNull(context);
    }
    
    @KafkaListener(topics = "${spring.kafka.consumer.topic}")
    public void listen(String data) {
    	System.out.println(data);
    }
    
    @SuppressWarnings("unchecked")
    public void send(ApplicationContext context){
      kafkaTemplate = (KafkaTemplate<Integer,String>)context.getBean(KafkaTemplate.class);
      
      kafkaTemplate.sendDefault(1, "hello world1");
    }
}
