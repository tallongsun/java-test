package com.dl.spring.websocket;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Service
public class MyWebSocketHandler implements WebSocketHandler,HandshakeInterceptor{
	public static Map<String, WebSocketSession> clients = new ConcurrentHashMap<String, WebSocketSession>();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MyWebSocketHandler.class);

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		LOGGER.info("Before Handshake");
		if (request instanceof ServletServerHttpRequest) {

			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
			HttpSession session = servletRequest.getServletRequest().getSession(false);
			
			String userId = (String) session.getAttribute("uid");
			attributes.put("uid", userId);
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		 response.getHeaders().set("Access-Control-Allow-Origin", "*");
	     LOGGER.info("After Handshake");
		
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String uid = (String) session.getAttributes().get("uid");
        if (clients.get(uid) == null) {
        	clients.put(uid, session);
        }
		
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message.getPayload().toString()));
        }
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        Iterator<Entry<String, WebSocketSession>> it = clients.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, WebSocketSession> entry = it.next();
            if (entry.getValue().getId().equals(session.getId())) {
            	clients.remove(entry.getKey());
            	LOGGER.info("Websocket:"+entry.getKey()+" has error...");
                break;
            }
        }
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		String sessionKey = (String)session.getAttributes().get("userid");
		LOGGER.info("Websocket:"+sessionKey+" has closed...");        
        Iterator<Entry<String, WebSocketSession>> it = clients.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, WebSocketSession> entry = it.next();
            if (entry.getValue().getId().equals(session.getId())) {
            	clients.remove(entry.getKey());
            	LOGGER.info("Websocket:"+entry.getKey()+" has removed...");
                break;
            }
        }
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}


}
