package com.dl.serialize;

public interface Serializer {
	public <T> byte[] serialize(T object);
	
	public <T> T deserialize(byte[] bytes,Class<T> clazz);
}
