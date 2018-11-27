package com.dl.serialize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dl.serialize.serializer.FastJsonSerializer;
import com.dl.serialize.serializer.JacksonSerializer;
import com.dl.serialize.serializer.JdkSerializer;
import com.dl.serialize.serializer.KryoSerializer;
import com.dl.serialize.serializer.MsgPackSerializer;
import com.dl.serialize.serializer.ProtoBufferSerializer;
import com.dl.serialize.serializer.XmlSerializer;

public class SerializeTest {
	private List<Serializer> serializers = new ArrayList<>();

	// 10万数据:proto3 75ms,fastjson 100ms,kryo 150ms,jackson 200ms,msgpack 400ms,jdk 800ms,xml 10s
	public static void main(String[] args) throws Exception {
		SerializeTest st = new SerializeTest();
		st.init();
		st.test(100000);
		
	}
	
	private void init(){
		this.serializers.add(new ProtoBufferSerializer());
		this.serializers.add(new FastJsonSerializer());
		this.serializers.add(new KryoSerializer());
		this.serializers.add(new MsgPackSerializer());
		this.serializers.add(new JdkSerializer());
		
		this.serializers.add(new JacksonSerializer());
		this.serializers.add(new XmlSerializer());
	}
    public void test(int count) {
    	Set<SimpleObject> set = new HashSet<>();
    	for(int i =0;i<count;i++){
			set.add(new SimpleObject("u"+i, "converted", "xxxxx"));
    	}
    	
    	for(Serializer ser : this.serializers){
        	Set<byte[]> bytesSet = new HashSet<>();
        	long size = 0;
        	
        	long start = System.currentTimeMillis();
        	for(SimpleObject c : set){
        		byte[] bytes = ser.serialize(c);
        		bytesSet.add(bytes);
        		size += bytes.length;
        	}
        	long end = System.currentTimeMillis();
        	System.out.println("ser name : "+ ser.getClass().getSimpleName() +",total size : "+size+"B");
        	System.out.println("ser time : "+(end - start) +"ms");
        	
        	start = System.currentTimeMillis();
        	for(byte[] bytes : bytesSet){
        		ser.deserialize(bytes, SimpleObject.class);
        	}
        	end = System.currentTimeMillis();
        	System.out.println("des time : "+(end - start) +"ms");
        	System.out.println("----------");
    	}
    	
	}
}
