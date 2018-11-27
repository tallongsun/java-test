package com.dl.db.perf.processor;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dl.db.perf.Processor;
import com.dl.db.perf.SimpleObject;

public class RedisProcessor implements Processor{
    private RedisTemplate<String,String> redisTemplate;
    
	private String keyName;
	
	public RedisProcessor(RedisTemplate<String, String> redisTemplate, String keyName) {
		this.redisTemplate = redisTemplate;
		this.keyName = keyName;
	}

	public void write(List<SimpleObject> allData) {
		redisTemplate.opsForValue().set(keyName, JSON.toJSONString(allData));
	}

	public List<SimpleObject> read() {
		String data = redisTemplate.opsForValue().get(keyName);
		return  JSON.parseObject(data,new TypeReference<List<SimpleObject>>(){});
	}
	
	public void clear(){
		redisTemplate.delete(keyName);
	}
}
