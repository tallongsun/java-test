package com.dl.ssoclient.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController 
public class CaasController {

	private RestOperations caasRestTemplate;
	
	public RestOperations getCaasRestTemplate() {
		return caasRestTemplate;
	}
	public void setCaasRestTemplate(RestOperations caasRestTemplate) {
		this.caasRestTemplate = caasRestTemplate;
	}
	
	@RequestMapping(value="/caas/reg",method={RequestMethod.POST, RequestMethod.GET})
	public String reg(){
		MultiValueMap<String,String> dataMap = new LinkedMultiValueMap<String, String>();  
        dataMap.add("user_name", "oauth");
        dataMap.add("email", "oauth@test.com");
        dataMap.add("mobile", "15998603807");
        dataMap.add("password", md5("1"));
        
        HttpEntity<MultiValueMap<String, String>> httpEntity = assembleForm(dataMap);
		ObjectNode obj = caasRestTemplate.postForObject("http://www.caas-test.com/api/v1/user/register", 
				httpEntity,
				ObjectNode.class);
		
		return obj.toString();
	}
	
	
	
	
	
	@RequestMapping(value="/caas/info",method={RequestMethod.POST, RequestMethod.GET})
	public String info(){
		
		MultiValueMap<String,String> dataMap = new LinkedMultiValueMap<String, String>();  
        dataMap.add("app_code", 1474118112189L+"");
        dataMap.add("timestamp", ""+System.currentTimeMillis());
        DefaultOAuth2AccessToken token = getToken();
        dataMap.add("access_token", token==null?"":token.getValue());
        dataMap.add("sign", sign(dataMap,"testapp-sec"));
        
        HttpEntity<MultiValueMap<String, String>> httpEntity = assembleForm(dataMap);
		ObjectNode obj = caasRestTemplate.postForObject("http://www.caas-test.com/api/v1/oauth2/info", 
				httpEntity,
				ObjectNode.class);
		
		return obj.toString();
	}
	
	@RequestMapping(value="/caas/check",method={RequestMethod.POST, RequestMethod.GET})
	public String check(){
		MultiValueMap<String,String> dataMap = new LinkedMultiValueMap<String, String>();  
        dataMap.add("app_code", 1474118112189L+"");
        dataMap.add("resource_code", "testres1-id");
        DefaultOAuth2AccessToken token = getToken();
        dataMap.add("access_token", token==null?"":token.getValue());
        dataMap.add("timestamp", ""+System.currentTimeMillis());
        dataMap.add("sign", sign(dataMap,"testapp-sec"));
        
        HttpEntity<MultiValueMap<String, String>> httpEntity = assembleForm(dataMap);
		ObjectNode obj = caasRestTemplate.postForObject("http://www.caas-test.com/api/v1/oauth2/check", 
				httpEntity,
				ObjectNode.class);
		
		return obj.toString();
	}
	
	@RequestMapping(value="/caas/bcheck",method={RequestMethod.POST, RequestMethod.GET})
	public String batchcheck(){
		MultiValueMap<String,String> dataMap = new LinkedMultiValueMap<String, String>();  
        dataMap.add("app_code", 1474118112189L+"");
        dataMap.add("resource_codes", "testres1-id");
        dataMap.add("resource_codes", "testres2-id");
        DefaultOAuth2AccessToken token = getToken();
        dataMap.add("access_token", token==null?"":token.getValue());
        dataMap.add("timestamp", ""+System.currentTimeMillis());
        dataMap.add("sign", sign(dataMap,"testapp-sec"));
        
        HttpEntity<MultiValueMap<String, String>> httpEntity = assembleForm(dataMap);
		ObjectNode obj = caasRestTemplate.postForObject("http://www.caas-test.com/api/v1/oauth2/batchcheck", 
				httpEntity,
				ObjectNode.class);
		
		return obj.toString();
	}
	
	@RequestMapping(value="/caas/logout")
	public String logout(){
		DefaultOAuth2AccessToken accessToken = getToken();
		if (accessToken != null) {
			accessToken.setExpiration(new Date(0L));
			accessToken.setRefreshToken(null);
		}

		return "ok";
	}
	
	
	
	
	
	
	private DefaultOAuth2AccessToken getToken(){
		return (DefaultOAuth2AccessToken)((OAuth2RestTemplate)caasRestTemplate).getAccessToken();
	}
	
	private HttpEntity<MultiValueMap<String, String>> assembleForm(MultiValueMap<String,String> dataMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<>(dataMap,headers);
		return httpEntity;
	}
	
	private String md5(final String str) {
		if (str != null) {
			try {
				return DigestUtils.md5Hex(str.getBytes("UTF-8")).toUpperCase();
			} catch (UnsupportedEncodingException e) {
				//
			}
		}
		return null;
	}
	
	private String sign(final MultiValueMap<String, String> paramsMap, final String secret) {
		TreeMap<String,String> params = new TreeMap<>();
		for(String param : paramsMap.keySet()){
			params.put(param, paramsMap.get(param).size()==1?paramsMap.getFirst(param):paramsMap.get(param).toString());
		}
		final StringBuilder buf = new StringBuilder();
		// add the secret
		buf.append(secret);
		// add the parameters
		for (final String key : params.keySet()) {
			final String value = params.get(key);
			if (value != null && value.trim().length() > 0) {
				buf.append(key).append(value);
			}
		}
		// add the secret
		buf.append(secret);
		// MD5
		return md5(buf.toString());
	}

}
