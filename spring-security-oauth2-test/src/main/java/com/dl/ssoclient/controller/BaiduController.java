package com.dl.ssoclient.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController 
public class BaiduController {
	private RestOperations baiduRestTemplate;

	public RestOperations getBaiduRestTemplate() {
		return baiduRestTemplate;
	}
	public void setBaiduRestTemplate(RestOperations baiduRestTemplate) {
		this.baiduRestTemplate = baiduRestTemplate;
	}
	
	@RequestMapping("/third_login/baidu")
	public String info(){
		ObjectNode obj = baiduRestTemplate.getForObject("https://openapi.baidu.com/rest/2.0/passport/users/getLoggedInUser", ObjectNode.class);
		return obj.toString();
	}
}
