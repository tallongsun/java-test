package com.dl.spring.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用层websocket协议的使用
 * 
 * @author tallong
 *
 * chrome console test:
   var ws = new WebSocket("ws://localhost:8080/ws?uid=123");
   ws.onopen = function(){ws.send("Test!"); };
   ws.onmessage = function(evt){console.log(evt.data);ws.close();};
   ws.onclose = function(evt){console.log("WebSocketClosed!");};
   ws.onerror = function(evt){console.log("WebSocketError!");};
 *
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}