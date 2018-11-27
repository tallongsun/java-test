package com.dl.resteasy.api;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dl.resteasy.service.FileService;

@Controller
public class FileResourceImpl implements FileResource {
	@Autowired
	private FileService fileService;

	@Override
	public Response fileUpload(MultipartFormDataInput input) {
		return fileService.uploadFile(input);
	}

	@Override
	public Response downloadFile(String fileName) {
		return fileService.downloadFile(fileName);
	}

}
