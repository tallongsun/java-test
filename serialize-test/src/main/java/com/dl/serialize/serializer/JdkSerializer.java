package com.dl.serialize.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.dl.serialize.Serializer;

public class JdkSerializer implements Serializer {

	@Override
	public <T> byte[] serialize(T object) {
		if (object == null) {
			return null;
		}
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(out)) {
			objectOutputStream.writeObject(object);
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
				ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
			Object object = objectInputStream.readObject();
			return (T) object;
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
