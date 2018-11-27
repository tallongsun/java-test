package com.dl.resteasy.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;


@Path("/")
public interface TestResource {
	@Path("/")
	@GET
    public String empty();

    @GET
    @Path("/1k")
    public String smallStr();

    @GET
    @Path("/10k")
    public String mediumStr();

    @GET
    @Path("/100k")
    public String largeStr();
}
