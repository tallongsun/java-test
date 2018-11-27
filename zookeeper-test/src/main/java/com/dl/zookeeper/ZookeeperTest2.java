package com.dl.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class ZookeeperTest2 {

	public static void main(String[] args) throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		// start
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 1);
		CuratorFramework zkClient = CuratorFrameworkFactory.builder().connectString("localhost:2181")
				.connectionTimeoutMs(5000).sessionTimeoutMs(5000).retryPolicy(retryPolicy).build();

		zkClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {

			@Override
			public void stateChanged(CuratorFramework client, ConnectionState newState) {
				try {
	                if (newState == ConnectionState.CONNECTED) {
	                    String name = register(zkClient);
					
	                    if (name != null) {
	                        System.out.println("Register with name "+name);
	                    }
	                } else if (newState == ConnectionState.RECONNECTED) {
	                    String name = register(zkClient);
	                    if (name != null) {
	                    	System.out.println("Re-register with name "+name);
	                    }
	                }else if (newState == ConnectionState.LOST){
	                	System.out.println("lost");
//	                	while(true){
//	                		try{
//	                			if(zkClient.getZookeeperClient().blockUntilConnectedOrTimedOut()){
//	                				String name = register(zkClient);
//	                				System.out.println("lost and register with name "+name);
//	                				break;
//	                			}
//	                		}catch(Exception e){
//	                			e.printStackTrace();
//	                		}
//	                	}
	                }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		zkClient.start();
		latch.await();
	}
	
	public static String register(CuratorFramework zkClient) throws Exception{
        if (zkClient.checkExists().forPath("/testworkers") == null) {
            zkClient.create().forPath("/testworkers", new byte[0]);
        }

        String path = new StringBuilder().append("/testworkers").append("/").append("1").toString();
        if (zkClient.checkExists().forPath(path) != null) {
            zkClient.delete().forPath(path);
        }
        zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(path, "127.0.0.1".getBytes());
        return "1234567";
	}

}
