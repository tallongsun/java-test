package com.dl.resteasy.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.dl.resteasy.vo.Bean;

@Service
public class SampleServiceImpl implements SampleService{
	private final Map<Long, Bean> beans = new HashMap<Long, Bean>();

	private final AtomicLong ids = new AtomicLong(1);

	@Override
	public Bean create(Bean bean) {
		bean.setId(this.ids.getAndIncrement());
		this.beans.put(bean.getId(), bean);
		return bean;
	}

	@Override
	public void update(long id, Bean bean) {
		Bean old = this.beans.get(id);
		if (old == null) {
			throw new EntityNotFoundException("the bean is NOT existed, id: " + id);
		}
		old.setKey(bean.getKey());
		old.setValue(bean.getValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.shangcm.learn.resteasy.service.SampleService#get(long)
	 */
	@Override
	public Bean get(long id) {
		Bean bean = this.beans.get(id);
		if (bean == null) {
			throw new EntityNotFoundException("the bean is NOT existed, id: " + id);
		}
		return bean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.shangcm.learn.resteasy.service.SampleService#list()
	 */
	@Override
	public List<Bean> list() {
		if (this.beans.isEmpty()) {
			return Collections.emptyList();
		}
		List<Bean> list = new ArrayList<Bean>();
		list.addAll(this.beans.values());
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.shangcm.learn.resteasy.service.SampleService#remove(long)
	 */
	@Override
	public void remove(long id) {
		Bean old = this.beans.get(id);
		if (old == null) {
			throw new EntityNotFoundException("the bean is NOT existed, id: " + id);
		}
		this.beans.remove(id);
	}
}
