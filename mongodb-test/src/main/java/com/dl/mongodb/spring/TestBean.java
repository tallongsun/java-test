package com.dl.mongodb.spring;

public class TestBean {
	private String id;
	
	private String name;
	
	private int type;

	public TestBean(String name,int type) {
		this.name = name;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "TestBean [id=" + id + ", name=" + name + ", type=" + type + "]";
	}
	
	
}
