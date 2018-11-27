package com.dl.resteasy.itest;

import java.util.Calendar;
import java.util.List;

import javax.ws.rs.BadRequestException;

import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.junit.Test;

import com.dl.resteasy.ValidationExceptionHandler.ErrorInfo;
import com.dl.resteasy.api.SampleResource;
import com.dl.resteasy.api.TestResource;
import com.dl.resteasy.vo.Bean;

public class APITest {
	@Test
	public void test() {
		// client
		ResteasyClient client = new ResteasyClientBuilder().httpEngine(
				new ApacheHttpClient4Engine(HttpClientBuilder.create().build())).build();

		// target
		ResteasyWebTarget target = client.target("http://localhost:8080/api/v1/");

		// proxy the resource
		TestResource r = target.proxy(TestResource.class);
		
		// test : 450QPS
		long start = Calendar.getInstance().getTimeInMillis();
		int n = 10000;
		for(int i=0;i<n;i++){
			r.empty();
		}
		long end = Calendar.getInstance().getTimeInMillis();
		System.out.println((double)n/(end-start)*1000);
	}

	@Test
	public void sampleTest() {
		try{
			// client
			ResteasyClient client = new ResteasyClientBuilder().httpEngine(
					new ApacheHttpClient4Engine(HttpClientBuilder.create().build())).build();
	
			// target
			ResteasyWebTarget target = client.target("http://localhost:8080/api/v1/");
	
			// proxy the resource
			SampleResource r = target.proxy(SampleResource.class);
	
			// create
			Bean b = new Bean();
			b.setKey("key-1");
			b.setValue("value-1");
			b = r.create(b);
			System.out.println("bean created: " + b);
	
			// get
			b = r.get(b.getId());
			System.out.println("bean loaded: " + b);
	
			// list
			List<Bean> list = r.list();
			System.out.println("beans listed: " + list);
	
			// update
			b.setValue(b.getValue() + "-1");
			r.update(b.getId(), b);
			System.out.println("bean updated: " + b);
	
			// get again
			b = r.get(b.getId());
			System.out.println("bean loaded: " + b);
	
			// remove
			r.remove(b.getId());
			System.out.println("bean removed");
	
			// list again
			list = r.list();
			System.out.println("beans listed: " + list);
		}catch(BadRequestException e){
			System.err.println(e.getResponse().readEntity(ErrorInfo.class));
		}
	}

}
