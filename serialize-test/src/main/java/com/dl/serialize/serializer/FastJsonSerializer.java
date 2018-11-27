package com.dl.serialize.serializer;



import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.dl.serialize.Serializer;

public class FastJsonSerializer implements Serializer{
	
	@Override
	public <T> byte[] serialize(T object) {
    	if(object == null){
    		return null;
    	}
		return JSON.toJSONBytes(object);
	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clazz) {
	  	if(bytes == null || bytes.length == 0){
    		return null;
    	}
		return JSON.parseObject(bytes, clazz);
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		FastJsonSerializer s = new FastJsonSerializer();
		
		System.out.println(new String(s.serialize(Arrays.asList(new Bean("test"))),"utf8"));
		System.out.println(new String(s.serialize(new CollectionBean(Arrays.asList(new Bean("test")))),"utf8"));
	}
	
	public  static class Bean{
		private String value;
		
		public Bean(String value){
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
		
		
	}
	
	public static class CollectionBean{
		private List<Bean> beans;
		
		public CollectionBean( List<Bean> beans){
			this.beans = beans;
		}
		
		public List<Bean> getBeans() {
			return beans;
		}

		public void setBeans(List<Bean> beans) {
			this.beans = beans;
		}
		
		
	}
}
