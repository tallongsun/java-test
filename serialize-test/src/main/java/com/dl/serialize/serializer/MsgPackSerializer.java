package com.dl.serialize.serializer;

import java.io.IOException;

import org.msgpack.MessagePack;

import com.dl.serialize.Serializer;

public class MsgPackSerializer implements Serializer{
	private MessagePack msgpack = new MessagePack();
	
	@Override
	public <T> byte[] serialize(T object) {
    	if(object == null){
    		return null;
    	}
		try {
			return msgpack.write(object);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clazz) {
	  	if(bytes == null || bytes.length == 0){
    		return null;
    	}
		
		try {
			return msgpack.read(bytes,clazz);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	

}
