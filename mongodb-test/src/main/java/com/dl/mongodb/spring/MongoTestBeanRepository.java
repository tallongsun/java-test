package com.dl.mongodb.spring;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MongoTestBeanRepository extends MongoRepository<TestBean, String>{
	@Query("{'name' : ?0}")
	public TestBean findByName(String name);
	
	public List<TestBean> findByType(int type);
}
