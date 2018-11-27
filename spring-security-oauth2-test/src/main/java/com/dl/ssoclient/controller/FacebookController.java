package com.dl.ssoclient.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController 
public class FacebookController {

	private RestOperations facebookRestTemplate;
	
	
	public RestOperations getFacebookRestTemplate() {
		return facebookRestTemplate;
	}
	public void setFacebookRestTemplate(RestOperations facebookRestTemplate) {
		this.facebookRestTemplate = facebookRestTemplate;
	}

	@RequestMapping("/")
	public String root(){
		return "index";
	}
	
	@RequestMapping("/fb/info")
	public String info(){
		ObjectNode obj = facebookRestTemplate.getForObject("https://graph.facebook.com/me/friends", ObjectNode.class);
		return obj.get("summary").toString();
	}
	
}
