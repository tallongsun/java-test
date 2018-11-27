package com.dl.serialize.serializer;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.dl.serialize.Serializer;

public class XmlSerializer implements Serializer {

	@Override
	public <T> byte[] serialize(T object) {
		if (object == null) {
			return null;
		}
        
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
	        try(XMLEncoder xe = new XMLEncoder(out, "UTF-8", true, 0)){
	            xe.writeObject(object);    
	        } 
	        return out.toByteArray();
        } catch (IOException e) {
        	throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(byte[] bytes, Class<T> clazz) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
				XMLDecoder xd = new XMLDecoder(byteArrayInputStream)) {
			Object object = xd.readObject();
			return (T) object;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
