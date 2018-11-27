package com.dl.druid.demo.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;

import com.mongodb.ServerAddress;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.connection.ClusterSettings;

public class WriteMongo implements IProcess {
	MongoClient mongoClient;

	MongoCollection<Document> collection;

	List<String> urls;

	public WriteMongo(String ds) {
		List<ServerAddress> lsa = new ArrayList<>();
		for (String item : urls) {
			lsa.add(new ServerAddress(item));
		}
		ClusterSettings clusterSettings = ClusterSettings.builder().hosts(lsa).build();
		MongoClientSettings settings = MongoClientSettings.builder().clusterSettings(clusterSettings).build();
		mongoClient = MongoClients.create(settings);

		MongoDatabase mdb = mongoClient.getDatabase(ds);
		collection = mdb.getCollection(ds);
	}

	@Override
	public void apply(List<HashMap<String, Object>> data) {
		List<InsertOneModel<Document>> res = new ArrayList<InsertOneModel<Document>>();
		for (HashMap<String, Object> row : data) {
			Document d = new Document();
			d.putAll(row);
			res.add(new InsertOneModel<Document>(d));
		}
		collection.bulkWrite(res, new SingleResultCallback<BulkWriteResult>() {
			@Override
			public void onResult(final BulkWriteResult result, final Throwable t) {
				System.out.println(result);
			}
		});
	}

}
