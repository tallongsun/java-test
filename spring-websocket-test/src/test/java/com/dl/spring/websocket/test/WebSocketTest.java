package com.dl.spring.websocket.test;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketTextListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.junit.Test;

public class WebSocketTest {
	private static final AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
	private static final String URL = "ws://localhost:8080/ws?uid=123";
	
	@Test
	public void test() throws Exception{
		createWebSocket(URL);
		
		Thread.sleep(5000);
	}
	
    public static WebSocket createWebSocket(String url) {
    	WebSocket websocket = null;
        try {
        	websocket = asyncHttpClient.prepareGet(url)
            		.execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketTextListener() {
            	          @Override
            	          public void onMessage(String message) {
            	        	  System.out.println(message);
            	          }

            	          @Override
            	          public void onOpen(WebSocket websocket) {
//            	              websocket.sendTextMessage(new TextMessage("test"));
            	              websocket.sendMessage("...");
            	          }

            	          @Override
            	          public void onClose(WebSocket websocket) {
            	          }

            	          @Override
            	          public void onError(Throwable t) {
            	          }
                    }).build()).get();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return websocket;
    }
}
