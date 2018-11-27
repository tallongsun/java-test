package com.dl.serialize.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.dl.serialize.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoSerializer implements Serializer {
	private Kryo kryo = new Kryo();

	@Override
	public <T> byte[] serialize(T object) {
		if (object == null) {
			return null;
		}
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				Output output = new Output(outputStream)) {
			kryo.writeObject(output, object);
			output.flush();
			return outputStream.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clazz) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
				Input input = new Input(byteArrayInputStream);) {
			return kryo.readObject(input, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
}
