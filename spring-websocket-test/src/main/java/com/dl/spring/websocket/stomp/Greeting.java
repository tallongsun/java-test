package com.dl.spring.websocket.stomp;

public class Greeting {
	private String content;

	public Greeting() {
	}

	public Greeting(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
}