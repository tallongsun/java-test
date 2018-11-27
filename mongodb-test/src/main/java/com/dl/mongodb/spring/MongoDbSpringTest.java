package com.dl.mongodb.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses=MongoTestBeanRepository.class)
public class MongoDbSpringTest implements CommandLineRunner{
	@Autowired
	private MongoTestBeanRepository repository;
	
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(MongoDbSpringTest.class, args);

		test(ctx);
	}
	
	private static void test(ApplicationContext ctx){
		//第二种方式：更灵活的支持各种mongo操作
		MongoTestBeanDao dao = ctx.getBean(MongoTestBeanDao.class);
//		
//		Criteria criteria = Criteria.where("name").exists(true);
//		Criteria criteria2 = Criteria.where("name").is("sun");
//		Query query = new Query(new Criteria().andOperator(new Criteria[]{criteria,criteria2}));
//		List<TestBean> list = dao.getMongoTemplate().find(query,TestBean.class);
//		System.out.println(list);
		
		//sharding 
		dao.getMongoTemplate().getCollection("testBean").createIndex(new BasicDBObject("name", 1));
		DBObject command = new BasicDBObject("shardCollection", "customer_journey.testBean").
				  append("key",new BasicDBObject("name", 1));
		System.out.println(dao.getMongoTemplate().getDb().getSisterDB("admin").command(command));
		for(int i=0;i<10;i++){
			System.out.println(dao.insert(new TestBean("y"+i,1)));
		}
		
		
//		System.out.println(dao.findOne("sun"));
//		System.out.println(dao.findList(1));
//		System.out.println(dao.fuzzyFindList("n"));
//		String command = "{\"aggregate\":\"test_bean\","
//				+ "\"pipeline\":[{ $match: { \"xtype\": 1} },{$group : {\"_id\":\"$xname\",\"count\":{$sum:1}}}]}";
//        BasicDBObject command = new BasicDBObject();
//        command.put("eval", "db.getCollectionNames().filter(function(c){return c.indexOf('tag_')==0})");
//        command.put("nolock", true);
//		System.out.println(dao.getMongoTemplate().executeCommand(command));
//		dao.delete("sun");
//		dao.delete("xin");
//		System.out.println(dao.findOne("sun"));
	}

	@Override
	public void run(String... args) throws Exception {
		//第一种方式：只支持简单的crud
//		repository.deleteAll();
//		
//		TestBean tb1 = new TestBean("sun",1);
//		tb1.setId("1");
//		repository.save(tb1);
//		tb1.setId("2");
//		tb1.setName("xin");
//		repository.save(tb1);
		
//		repository.save(new TestBean("sun",1));
//		repository.save(new TestBean("xin",1));
//		
//		System.out.println("TestBeans found with findAll():");
//		System.out.println("-------------------------------");
//		for (TestBean tb : repository.findAll()) {
//			System.out.println(tb);
//		}
//		System.out.println();
//		
//		System.out.println("TestBean found with findByName('xin'):");
//		System.out.println("--------------------------------");
//		System.out.println(repository.findByName("xin"));
//
//		System.out.println("TestBeans found with findByType(1):");
//		System.out.println("--------------------------------");
//		for (TestBean tb : repository.findByType(1)) {
//			System.out.println(tb);
//		}
	}

}
