package com.dl.spring.integration.service.mq;

import org.springframework.stereotype.Service;

@Service
public class MessageListener {
    public void processMessage( String message ){

        System.out.println( "MessageListener::::::Received message: " + message );
    }
}
