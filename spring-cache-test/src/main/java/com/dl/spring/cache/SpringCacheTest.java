package com.dl.spring.cache;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import com.dl.spring.cache.po.User;

@SpringBootApplication
@EnableCaching
@PropertySources({ @PropertySource(value = "classpath:jedis-cluster.properties")})
@ImportResource({"classpath:cache-cluster.xml"})
public class SpringCacheTest {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception{
		ApplicationContext ctx = SpringApplication.run(SpringCacheTest.class, args);
		
		RedisTemplate<String, Object> redisTemplate = ctx.getBean("redisTemplateCache",RedisTemplate.class);
		ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
//		SetOperations<String, Object> setOps = redisTemplate.opsForSet();
//		
//		valueOps.set("testkey", new User(1L,"sun","sun@test.com"));
//		System.out.println(valueOps.get("testkey"));
//		
		System.out.println(valueOps.increment("testnum",1));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hhmm");
		Date date = sdf.parse("20171016 0952");
		Date now = new Date();
		long time = date.getTime()-now.getTime();
		redisTemplate.expire("testnum", time,TimeUnit.MILLISECONDS);
//		
//		setOps.add("testset", new User(1L,"sun","sun@test.com"));
//		setOps.add("testset", new User(2L,"sun2","sun2@test.com"));
//		System.out.println(setOps.members("testset"));
		
//		redisTemplate.delete("testkey");
//		redisTemplate.delete("testnum");
//		redisTemplate.delete("testset");
//		
//		redisTemplate.delete("testmap");
//		HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
//		long start = Calendar.getInstance().getTimeInMillis();
////		for(int i=0;i<1000000;i++){
////			hashOps.put("testmap", "1234567890abcdefghi_"+i , "1234567890abcdefghijklmnopqrstuvwxyz_"+i);
////		}
//		long end = Calendar.getInstance().getTimeInMillis();
//		System.out.println((end-start)/1000);
//		
//		start = Calendar.getInstance().getTimeInMillis();
//		Set<Object> set = hashOps.keys("9163c3a6caf54d528aac46537d2c92ba:1");
//		end = Calendar.getInstance().getTimeInMillis();
//		System.out.println("---"+(end-start));
//		
//		start = Calendar.getInstance().getTimeInMillis();
//		Map<Object,Object> vset = hashOps.entries("9163c3a6caf54d528aac46537d2c92ba:1");
//		end = Calendar.getInstance().getTimeInMillis();
//		
//		System.out.println("---"+(end-start));
//		for(Object f : set){
//			hashOps.get("testmap", f);
//		}
//		end = Calendar.getInstance().getTimeInMillis();
//		System.out.println(end-start);
		
	}
}
