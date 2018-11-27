package com.dl.serialize.serializer;



import java.io.IOException;

import com.dl.serialize.Serializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSerializer implements Serializer{
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public <T> byte[] serialize(T object) {
    	if(object == null){
    		return null;
    	}
        try {
            return mapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
        	throw new RuntimeException(e.getMessage(), e);
        }
	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clazz) {
	  	if(bytes == null || bytes.length == 0){
    		return null;
    	}
        try {
            return mapper.readValue(bytes, clazz);
        } catch (IOException e) {
        	throw new RuntimeException(e.getMessage(), e);
        }
	}
}
