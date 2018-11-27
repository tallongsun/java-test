package com.dl.stream;

public class Transaction {
	public static int GEOCERY = 1;
	
	private int id;
	private int type;
	private int value;
	
	public Transaction(int id, int type, int value) {
		super();
		this.id = id;
		this.type = type;
		this.value = value;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Transaction [id=" + id + ", type=" + type + ", value=" + value + "]";
	}
	
	
}
