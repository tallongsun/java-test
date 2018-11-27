package com.dl.mongodb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoDbTest {
	private static DB db;

	private static GridFS gridFS = null;

	public static void main(String[] args) throws Exception {
		MongoClient mongo = new MongoClient("127.0.0.1", 27017);
		db = mongo.getDB("jike");
		Set<String> collectionNames = db.getCollectionNames();
		for (String name : collectionNames) {
			System.out.println("collectionName===" + name);
		}

		gridFS = new GridFS(db);

		String file = "test";
		if (!new File(file).exists()) {
			System.out.println("file is not exists.");
		}
		String fileName = "test";
		// 把文件保存到gridfs中，并以文件的md5值为id
		save(new FileInputStream(file), fileName, fileName);
		
		// 据文件名从gridfs中读取到文件
		GridFSDBFile gridFSDBFile = getByFileName(fileName);
		if (gridFSDBFile != null) {
			System.out.println("filename:" + gridFSDBFile.getFilename());
			System.out.println("md5:" + gridFSDBFile.getMD5());
			System.out.println("length:" + gridFSDBFile.getLength());
			System.out.println("uploadDate:" + gridFSDBFile.getUploadDate());
			System.out.println("--------------------------------------");
			gridFSDBFile.writeTo(new FileOutputStream("test_out"));
		} else {
			System.out.println("can not get file by name:" + fileName);
		}
	}

	public static void save(InputStream in, Object id, String fileName) {
		DBObject query = new BasicDBObject("_id", id);
		GridFSDBFile gridFSDBFile = gridFS.findOne(query);
		if (gridFSDBFile == null) {
			GridFSInputFile gridFSInputFile = gridFS.createFile(in);
			gridFSInputFile.setId(id);
			gridFSInputFile.setFilename(fileName);
			// gridFSInputFile.setChunkSize();
			// gridFSInputFile.setContentType();
			// gridFSInputFile.setMetaData();
			gridFSInputFile.save();
		}
	}

	public static GridFSDBFile getById(Object id) {
		DBObject query = new BasicDBObject("_id", id);
		GridFSDBFile gridFSDBFile = gridFS.findOne(query);
		return gridFSDBFile;
	}

	public static GridFSDBFile getByFileName(String fileName) {
		DBObject query = new BasicDBObject("filename", fileName);
		GridFSDBFile gridFSDBFile = gridFS.findOne(query);
		return gridFSDBFile;
	}

}
