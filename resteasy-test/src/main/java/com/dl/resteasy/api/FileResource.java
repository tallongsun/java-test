package com.dl.resteasy.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;


@Path("file")
public interface FileResource {
	@Path("/upload")
	@POST
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces({ MediaType.APPLICATION_JSON })
	Response fileUpload(MultipartFormDataInput input);

	
	@Path("/download")
	@GET
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	Response downloadFile(@QueryParam(value = "fileName") String fileName);
}
