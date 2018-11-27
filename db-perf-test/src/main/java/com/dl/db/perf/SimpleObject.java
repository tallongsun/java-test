package com.dl.db.perf;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class SimpleObject {
    private String vertex;
    private String uid;
    private String status;
    private String path;
    
	public String getVertex() {
		return vertex;
	}
	public void setVertex(String vertex) {
		this.vertex = vertex;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
    
    
}
