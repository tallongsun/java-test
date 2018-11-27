package com.dl.ssoclient.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.client.RestOperations;

import com.dl.ssoclient.controller.BaiduController;
import com.dl.ssoclient.controller.CaasController;
import com.dl.ssoclient.controller.FacebookController;

@Configuration
public class WebConfig{

	@Bean
	public FacebookController facebookController(RestOperations facebookRestTemplate) {
		FacebookController controller = new FacebookController();
		controller.setFacebookRestTemplate(facebookRestTemplate);
		return controller;
	}
	
	@Bean
	public BaiduController baiduController(RestOperations baiduRestTemplate) {
		BaiduController controller = new BaiduController();
		controller.setBaiduRestTemplate(baiduRestTemplate);
		return controller;
	}
	
	@Bean
	public CaasController caasController(RestOperations caasRestTemplate) {
		CaasController controller = new CaasController();
		controller.setCaasRestTemplate(caasRestTemplate);
		return controller;
	}
	
	@Configuration
	@EnableOAuth2Client
	protected static class FacebookResourceConfiguration {
		@Bean
		public OAuth2RestTemplate facebookRestTemplate(OAuth2ClientContext clientContext) {
			OAuth2RestTemplate template = new OAuth2RestTemplate(facebook(), clientContext);
			MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
			converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,MediaType.valueOf("text/javascript")));
			template.setMessageConverters(Arrays.<HttpMessageConverter<?>> asList(converter));
			return template;
		}
		
		@Bean
		public OAuth2ProtectedResourceDetails facebook() {
			AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
			details.setId("facebook");
//			details.setClientId("233668646673605");
//			details.setClientSecret("33b17e044ee6a4fa383f46ec6e28ea1d");
			details.setClientId("146430705818054");
			details.setClientSecret("a0934fd413e797b2e624f21441ead81b");
			details.setAccessTokenUri("https://graph.facebook.com/oauth/access_token");
			details.setUserAuthorizationUri("https://www.facebook.com/dialog/oauth");
			details.setTokenName("oauth_token");
			details.setAuthenticationScheme(AuthenticationScheme.query);
			details.setClientAuthenticationScheme(AuthenticationScheme.form);
			return details;
		}
	}
	
	@Configuration
	@EnableOAuth2Client
	protected static class BaiduResourceConfiguration {
		@Bean
		public OAuth2RestTemplate baiduRestTemplate(OAuth2ClientContext clientContext) {
			OAuth2RestTemplate template = new OAuth2RestTemplate(baidu(), clientContext);
			template.setAccessTokenProvider(new CsrfDisabledTokenProvider());
			MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
			converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,MediaType.valueOf("text/javascript")));
			template.setMessageConverters(Arrays.<HttpMessageConverter<?>> asList(converter));
			return template;
		}
		
		@Bean
		public OAuth2ProtectedResourceDetails baidu() {
			AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
			details.setId("baidu");
			details.setClientId("loNcMgol4XiQQEEj7AYEvTYS");
			details.setClientSecret("gKAZltQNoVXNWXBwU1bQ6stLrhH0gITU");
			details.setAccessTokenUri("https://openapi.baidu.com/oauth/2.0/token");
			details.setUserAuthorizationUri("http://openapi.baidu.com/oauth/2.0/authorize");
			details.setTokenName("access_token");
			details.setAuthenticationScheme(AuthenticationScheme.query);
			details.setClientAuthenticationScheme(AuthenticationScheme.form);
			return details;
		}
	}
	
	@Configuration
	@EnableOAuth2Client
	protected static class CaasResourceConfiguration {
		@Bean
		public OAuth2RestTemplate caasRestTemplate(OAuth2ClientContext clientContext) {
			OAuth2RestTemplate template = new OAuth2RestTemplate(caas(), clientContext);
			template.setAccessTokenProvider(new AccessTokenProviderChain(Arrays.<AccessTokenProvider> asList(
					new CsrfDisabledTokenProvider())));
			MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
			jsonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,MediaType.valueOf("text/javascript")));
			FormHttpMessageConverter formConverter = new FormHttpMessageConverter();
			formConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED));
			template.setMessageConverters(Arrays.<HttpMessageConverter<?>> asList(jsonConverter,formConverter));
			return template;
		}
		
		@Bean
		public OAuth2ProtectedResourceDetails caas() {
			AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
			details.setId("caas");
			details.setClientId("1474118112189");
			details.setClientSecret("testapp-sec");
			details.setAccessTokenUri("http://www.caas-test.com/api/v1/oauth2/token");
			details.setUserAuthorizationUri("http://www.caas-test.com/api/v1/oauth2/authorize");
			details.setTokenName("access_token");
			details.setAuthenticationScheme(AuthenticationScheme.form);
			details.setClientAuthenticationScheme(AuthenticationScheme.form);
			return details;
		}
	}
}
