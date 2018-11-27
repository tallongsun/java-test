package com.dl.db.perf.processor;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dl.db.perf.Processor;
import com.dl.db.perf.SimpleObject;

public class MongoProcessor implements Processor{
    private MongoTemplate mongoTemplate;
	
	private String collectionName;
	
	private boolean isBigField = true;

	public MongoProcessor(MongoTemplate mongoTemplate, String collectionName,boolean isBigField) {
		this.mongoTemplate = mongoTemplate;
		this.collectionName = collectionName;
		this.isBigField = isBigField;
	}

	public void write(List<SimpleObject> allData) {
		if(isBigField){
			Document d = new Document();
			d.put("key", collectionName);
			d.put("value", JSON.toJSONString(allData));
			mongoTemplate.insert(d,collectionName);
		}else{
	    	BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkMode.UNORDERED,collectionName);
	    	bulkOperations.insert(allData);
	    	bulkOperations.execute();
		}

	}

	public List<SimpleObject> read() {
		if(isBigField){
			Query query = new Query(Criteria.where("key").is(collectionName));
			List<Document> res = mongoTemplate.find(query,Document.class,collectionName);
			return JSON.parseObject(res.get(0).get("value").toString(),new TypeReference<List<SimpleObject>>(){});
		}else{
	    	Query query = new Query(Criteria.where("status").is("CONVERTED").and("vertex").is("n1"));
			return  mongoTemplate.find(query,SimpleObject.class,collectionName);
		}

	}
	
	public void clear(){
		mongoTemplate.getCollection(collectionName).drop();
	}

}
