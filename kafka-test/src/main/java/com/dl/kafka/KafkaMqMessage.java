package com.dl.kafka;

public class KafkaMqMessage {
	private long timestamp;
	private String usercode;
	private Object paramObj;
	private String param;
	
	private double value;
	
	public KafkaMqMessage(){
		
	}
	
	public KafkaMqMessage(long timestamp, String usercode, String param) {
		this.timestamp = timestamp;
		this.usercode = usercode;
		this.param = param;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getUsercode() {
		return usercode;
	}
	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}
	
	
	public Object getParamObj() {
		return paramObj;
	}

	public void setParamObj(Object paramObj) {
		this.paramObj = paramObj;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}


	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}


	public static class KeyValueParam{
		private String key;
		private String value;
		
		public KeyValueParam(String key, String value) {
			this.key = key;
			this.value = value;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return "KeyValueParam [key=" + key + ", value=" + value + "]";
		}
		
	}

	@Override
	public String toString() {
		return "KafkaMqMessage [timestamp=" + timestamp + ", usercode=" + usercode + ", param=" + param + "]";
	}
	
	
}
