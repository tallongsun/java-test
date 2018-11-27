package com.dl.db.perf;

public class KeyValue {
	private String keyField;
	private String valueField;
	
	
	public KeyValue(String keyField, String valueField) {
		this.keyField = keyField;
		this.valueField = valueField;
	}
	
	public String getKeyField() {
		return keyField;
	}
	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}
	public String getValueField() {
		return valueField;
	}
	public void setValueField(String valueField) {
		this.valueField = valueField;
	}
	
	
	
}
