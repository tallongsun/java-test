package com.dl.spring.mvc.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.dl.spring.mvc.service.TestService;

@RestController
public class TestController {
	@Autowired
	private TestService testService;
	@Autowired
	private ServletContext servletContext;
	
	@RequestMapping("/test")
	public String testSessionPass(HttpSession session){
		session.setAttribute("test", "testval");
		return testService.testSessionPass();
	}
	
	@RequestMapping("/upload")
	//从目前验证来看，chrome显示的上传百分比只是把文件发送到socket中，client和server的os内存都会增长，
	//但是server的tomcat(jvm)开始会确认下是否可以接受（通过头?），然后等到整个文件都传完了才将整个file（附上上该页面其他参数作为一个http请求？该文件暂存到临时文件？）传递给这个user-servcie（jvm）
	public ModelAndView uploadFile(HttpServletRequest request,HttpServletResponse response){
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(!isMultipart){
			return null;
		}
		
		
//		//1.traditional:file items must be stored somewhere before they are actually accessable by the user. memory and time consuming
//		DiskFileItemFactory factory = new DiskFileItemFactory();
//		factory.setSizeThreshold(Integer.MAX_VALUE);
//
//		// Configure a repository (to ensure a secure temp location is used)
//		File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
//		factory.setRepository(repository);
//
//		ServletFileUpload upload = new ServletFileUpload(factory);
//		upload.setSizeMax(Long.MAX_VALUE);
//		
//
//		try {
//			List<FileItem> items = upload.parseRequest(request);
//			Iterator<FileItem> iter = items.iterator();
//			while (iter.hasNext()) {
//			    FileItem item = iter.next();
//				if (item.isFormField()) {
//					String name = item.getFieldName();
//					String value = item.getString();
//				} else {
//				    String fieldName = item.getFieldName();
//				    String fileName = item.getName();
//				    String contentType = item.getContentType();
//				    boolean isInMemory = item.isInMemory();
//				    long sizeInBytes = item.getSize();
//				    //byte[] data = item.get();//Process a file upload in memory
//				    boolean writeToFile = true;
//				    if (writeToFile) {
//				        File uploadedFile = new File("src/main/resources/static/test");
//				        //will attempt to rename the file to the specified destination, if the data is already in a temporary file
//						item.write(uploadedFile);
//				    } else {
//				        InputStream uploadedStream = item.getInputStream();
//				        //process the content as a stream
//				        uploadedStream.close();
//				    }
//				}
//			}
//			System.out.println(items);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		//2.stream:trade a little bit of convenience for optimal performance and a low memory profile
		ServletFileUpload upload = new ServletFileUpload();
		try {
			FileItemIterator iter = upload.getItemIterator(request);
			
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
			    String name = item.getFieldName();
			    InputStream stream = item.openStream();
			    if(item.isFormField()){
			        System.out.println("Form field " + name + " with value " + Streams.asString(stream) + " detected.");
			    }else{
			        System.out.println("File field " + name + " with file name " + item.getName() + " detected.");
		            // Process the input stream
			    }
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		return null;
	}
}
