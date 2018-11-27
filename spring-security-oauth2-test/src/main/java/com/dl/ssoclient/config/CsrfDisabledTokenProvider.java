package com.dl.ssoclient.config;

import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;

public class CsrfDisabledTokenProvider extends AuthorizationCodeAccessTokenProvider {
	public CsrfDisabledTokenProvider(){
		this.setStateMandatory(false);
	}
}
