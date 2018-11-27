package com.dl.db.perf;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import com.dl.db.perf.dao.KeyValueMapper;
import com.dl.db.perf.dao.SimpleObjectMapper;
import com.dl.db.perf.processor.EsProcessor;
import com.dl.db.perf.processor.MongoProcessor;
import com.dl.db.perf.processor.MysqlProcessor;
import com.dl.db.perf.processor.RedisProcessor;

@SpringBootApplication
@PropertySources({ @PropertySource("classpath:/elasticsearch.properties"),
	@PropertySource("classpath:/datasource.properties"),
	@PropertySource("classpath:/mongo.properties")})
@ImportResource({"classpath:/datasource.xml","classpath:/mongo.xml"})
public class DbPerfTest implements CommandLineRunner{
	private List<Processor> processors = new ArrayList<>();
	
	@Autowired
    private MongoTemplate mongoTemplate;
    
	@Autowired
    private RedisTemplate<String,String> redisTemplate;
	
	@Autowired
	private Client esClient;
	
	@Autowired
	private SimpleObjectMapper simpleObjectMapper;
	@Autowired
	private KeyValueMapper keyValueMapper;
    
	public static void main(String[] args) {
		SpringApplication.run(DbPerfTest.class, args);
		
	}

	@Override
	public void run(String... args) throws Exception {
		init();
		test(1000);//100万数据，大字段的实现方式会有问题，文档或者字段超出大小
	}
	
	public void init(){
//		this.processors.add(new RedisProcessor(redisTemplate, "cj:t1:j1:3d:20170830:n1:CONVERTED"));
//		this.processors.add(new MongoProcessor(mongoTemplate,"cj_t1_j1_3d_20170830",true));
//		this.processors.add(new EsProcessor(esClient, "cj",true));
		this.processors.add(new MysqlProcessor(simpleObjectMapper,keyValueMapper,true));
	}

	public void test(int count){
    	List<SimpleObject> list = new ArrayList<>();
    	for(int i =0;i<count;i++){
	    	SimpleObject jpt = new SimpleObject();
	    	jpt.setStatus("CONVERTED");
	    	jpt.setUid("u"+i);
	    	jpt.setPath("xxxxx");
	    	jpt.setVertex("n1");
	    	list.add(jpt);
    	}
    	
    	for(Processor processor : this.processors){
    		long start = System.currentTimeMillis();
    		processor.write(list);
    		long end = System.currentTimeMillis();
        	System.out.println("processor name : "+ processor.getClass().getSimpleName());
        	System.out.println("write time : "+(end - start) +"ms");
        	
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	
        	start = System.currentTimeMillis();
    		List<SimpleObject> result = processor.read();
    		end = System.currentTimeMillis();
    		System.out.println("count : "+result.size());
    		System.out.println("read time : "+(end - start) +"ms");
    		
    		processor.clear();
    	}
	}
}
