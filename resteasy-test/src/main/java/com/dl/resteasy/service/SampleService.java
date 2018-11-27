package com.dl.resteasy.service;

import java.util.List;

import com.dl.resteasy.vo.Bean;

public interface SampleService {
	Bean create(Bean bean);

	void update(long id, Bean bean);

	Bean get(long id);

	List<Bean> list();

	void remove(long id);
}
