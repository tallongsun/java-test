package com.dl.resteasy.api;

import java.util.List;

import javax.ws.rs.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dl.resteasy.service.EntityNotFoundException;
import com.dl.resteasy.service.SampleService;
import com.dl.resteasy.vo.Bean;

@Controller
public class SampleResourceImpl implements SampleResource{
	@Autowired
	private SampleService sampleService;
	
	@Override
	public Bean create(Bean bean) {
		return this.sampleService.create(bean);
	}

	@Override
	public void update(long id, Bean bean) {
		try {
			this.sampleService.update(id, bean);
		} catch (EntityNotFoundException e) {
			throw new NotFoundException(e);
		}
	}

	@Override
	public Bean get(long id) {
		try {
			return this.sampleService.get(id);
		} catch (EntityNotFoundException e) {
			throw new NotFoundException(e);
		}
	}

	@Override
	public List<Bean> list() {
		return this.sampleService.list();
	}

	@Override
	public void remove(long id) {
		try {
			this.sampleService.remove(id);
		} catch (EntityNotFoundException e) {
			throw new NotFoundException(e);
		}
	}

}
