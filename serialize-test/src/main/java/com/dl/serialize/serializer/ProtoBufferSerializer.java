package com.dl.serialize.serializer;

import java.util.concurrent.ConcurrentHashMap;

import com.dl.serialize.Serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeEnv;
import io.protostuff.runtime.RuntimeSchema;

public class ProtoBufferSerializer implements Serializer{
	private final static ConcurrentHashMap<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();
	
	@Override
    @SuppressWarnings("unchecked")
	public <T> byte[] serialize(T object){
    	if(object == null){
    		return null;
    	}
		Class<T> cls = (Class<T>) object.getClass();
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		try{
			Schema<T> schema = getSchema(cls);
			byte[] bytes = ProtostuffIOUtil.toByteArray(object, schema, buffer);
			return bytes;
		}finally{
			buffer.clear();
		}
	}
    
	@Override
    public <T> T deserialize(byte[] bytes,Class<T> clazz){
    	if(bytes == null || bytes.length == 0){
    		return null;
    	}
    	Schema<T> schema = getSchema(clazz);
    	T message = schema.newMessage();
    	ProtostuffIOUtil.mergeFrom(bytes, message, schema);
    	return message;
    }
	
    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> clazz) {
		Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz, RuntimeEnv.ID_STRATEGY);
            cachedSchema.put(clazz, schema);
        }
        return schema;
    }
    
    
}
