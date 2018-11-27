package com.dl.resteasy.service;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

public interface FileService {
	Response uploadFile(MultipartFormDataInput fileInput);
	
	Response downloadFile(String fileName);
}
