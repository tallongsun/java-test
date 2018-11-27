package com.dl.jedis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisTest {
	public static void main(String[] args) throws Exception{
		testSentinel();
	}
	
	public static void testSentinel() {
        Set<String> sentinels = new HashSet<String>();  
        sentinels.add("127.0.0.1:5000");
        sentinels.add("127.0.0.1:5001");
        sentinels.add("127.0.0.1:5002");
        JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinels);
        
        try(Jedis jedis = pool.getResource()){
        		jedis.set("jedis", "jedis");
        }
        pool.close();
	}

	// A single Jedis instance is not threadsafe!
	public static void testJedis() {
		Jedis jedis = new Jedis("localhost");
		jedis.set("foo", "bar");
		String value = jedis.get("foo");
		System.out.println(value);

		jedis.close();
	}

	public static void testJedisReconnect() {
		for(int i=0;i<10;i++) {
			try {
				Thread.sleep(30000);
				testJedisPool();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// a threadsafe pool of network connections
	public static void testJedisPool() {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

		//auto-closed,If you don't close then it doesn't release back to pool and you can't get a new resource from pool.
		try (Jedis jedis = pool.getResource()) {
			/// ... do stuff here ... for example
			jedis.set("foo", "bar");
			String foobar = jedis.get("foo");
			System.out.println(foobar);
			
			jedis.zadd("sose", 0, "car");
			jedis.zadd("sose", 0, "bike");
			Set<String> sose = jedis.zrange("sose", 0, -1);
			System.out.println(sose);
		}//close happens,If Jedis was borrowed from pool, it will be returned to pool with proper method since it already determines there was JedisConnectionException occurred. If Jedis wasn't borrowed from pool, it will be disconnected and closed.
		
		pool.close();
	}
	
	public static void testTransaction() {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
		try (Jedis jedis = pool.getResource()) {
			Transaction t = jedis.multi();
			t.set("fool", "bar"); 
			Response<String> result1 = t.get("fool");
			//not like database，a Response Object does not contain the result before t.exec() is called (it is a kind of a Future)
			t.zadd("foo2", 1, "barowitch"); 
			t.zadd("foo2", 0, "barinsky"); 
			t.zadd("foo2", 0, "barikoviev");
			Response<Set<String>> sose = t.zrange("foo2", 0, -1); 
			
			List<Object> allResults = t.exec();   
			
			System.out.println(allResults);
			System.out.println(result1.get());
			System.out.println(sose.get());
		}
		
		pool.close();
	}
	
	public static void testPipelining() {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
		try (Jedis jedis = pool.getResource()) {
			//like batch, have better performance
			Pipeline p = jedis.pipelined();
			p.set("fool", "bar"); 
			p.zadd("foo2", 1, "barowitch"); 
			p.zadd("foo2", 0, "barinsky"); 
			p.zadd("foo2", 0, "barikoviev");
			Response<String> pipeString = p.get("fool");
			Response<Set<String>> sose = p.zrange("foo2", 0, -1);
			p.sync(); 
	
			System.out.println(pipeString.get());
			System.out.println(sose.get());
		}
		
		pool.close();
	}
	
	public static void testPubSub() throws Exception {
		class MyListener extends JedisPubSub {
			public void onMessage(String channel, String message) {
				System.out.println("channel:"+channel+",message:"+message);
			}

			public void onSubscribe(String channel, int subscribedChannels) {
			}

			public void onUnsubscribe(String channel, int subscribedChannels) {
			}

			public void onPSubscribe(String pattern, int subscribedChannels) {
			}

			public void onPUnsubscribe(String pattern, int subscribedChannels) {
			}

			public void onPMessage(String pattern, String channel, String message) {
			}
		}
		MyListener l = new MyListener();
		
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
		
		for(int i=0;i<10;i++) {
			try (Jedis jedis = pool.getResource()) {
				jedis.publish( "foo","ptest"+i);
			}
		}
		
		CountDownLatch latch = new CountDownLatch(1);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					startListen(l, pool);
				}finally {
					latch.countDown();
				}
			}

			private void startListen(MyListener l, JedisPool pool) {
				try (Jedis jedis = pool.getResource()) {
					//blocking operation because it will poll Redis for responses on the thread that calls subscribe.
					jedis.subscribe(l, "foo");
				}catch(JedisConnectionException e) {
					e.printStackTrace();
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
					}
					startListen(l,pool);
				}
			}
		}).start();

		for(int i=0;i<10;i++) {
			try (Jedis jedis = pool.getResource()) {
				jedis.publish( "foo","atest"+i);
			}
		}
		
		latch.await();
		pool.close();
	}
}
