package com.dl.resteasy.api;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.dl.resteasy.vo.Bean;


@Path("sample")
public interface SampleResource {
	@Path("/bean")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	Bean create(@Valid Bean bean);

	@Path("/bean/{id}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	void update(@PathParam("id") long id, @Valid Bean bean);

	@Path("/bean/{id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	Bean get(@PathParam("id") long id);

	@Path("/bean")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	List<Bean> list();

	@Path("/bean/{id}")
	@DELETE
	void remove(@PathParam("id") long id);
}
