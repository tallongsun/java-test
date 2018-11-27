package com.dl.mongodb.spring;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.CommandResult;
import com.mongodb.WriteResult;

@Repository
public class MongoTestBeanDao{
    
    @Autowired
    public MongoTemplate mongoTemplate;
    
    public MongoTemplate getMongoTemplate(){
    	return this.mongoTemplate;
    }
    
    public void executeCommand(String jsonCommand){
    	CommandResult result = mongoTemplate.executeCommand(jsonCommand);
    	System.out.println(result.toJson());
    	System.out.println(result.getString("result"));
    }
    
    public void test(){
    	this.mongoTemplate.getCollection("").getDB();
    }
    
    public boolean insert(TestBean testBean){
    	Query query = new Query(Criteria.where("name").is(testBean.getName()));
    	WriteResult result = mongoTemplate.upsert(query,buildUpdate(testBean),TestBean.class);
    	return result.getN()==1;
    }
    
    public TestBean findOne(String name){
    	Query query = new Query(Criteria.where("name").is(name));
    	TestBean bean = mongoTemplate.findOne(query, TestBean.class);
    	return bean;
    }
    
    public List<TestBean> findList(int type){
    	Query query = new Query(Criteria.where("type").is(type));
    	List<TestBean> beans = mongoTemplate.find(query, TestBean.class);
    	return beans;
    }
    
    public List<TestBean> fuzzyFindList(String name){
    	Query query = new Query(Criteria.where("name").regex(name));
    	List<TestBean> beans = mongoTemplate.find(query, TestBean.class);
    	return beans;
    }
    
    public void delete(String name){
    	Query query = new Query(Criteria.where("name").is(name));
    	mongoTemplate.findAllAndRemove(query, TestBean.class);
    }
    
    private Update buildUpdate(TestBean testBean){
    	Update update = new Update();
    	update.set("name", testBean.getName());
    	update.set("type", testBean.getType());
    	return update;
    }
}
