package com.dl.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;

public class ZookeeperTest {

	public static void main(String[] args) throws Exception{
		//start
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
		CuratorFramework zkClient = CuratorFrameworkFactory.builder()
				.connectString("localhost:2181").retryPolicy(retryPolicy).build();
		zkClient.start();
		
		String path = "/configserver/app1/database_config";
		String data = "test-data";
		
		//clear
		if (zkClient.checkExists().forPath(path) != null) {
			zkClient.delete().deletingChildrenIfNeeded().forPath(path);
		}
		
//		//subscribe
//		TreeCache cache = new TreeCache(zkClient, path);
//		cache.start();
//		cache.getListenable().addListener(new TreeCacheListener() {
//			@Override
//			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
//				Type type = event.getType();
//				if(event.getData()!=null && event.getData().getData()!=null){
//					System.out.println(type+","+new String(event.getData().getPath())+","+new String(event.getData().getData()));
//				}
//			}
//		});
		

		
		//write
		if (zkClient.checkExists().forPath(path) == null) {
			// create
			zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, data.getBytes());
		} else {
			// update
			zkClient.setData().forPath(path, data.getBytes());
		}
		
		Thread.sleep(600000);
//		
//		//load
//		if (zkClient.checkExists().forPath(path) != null) {
//			String result = new String(zkClient.getData().forPath(path));
//			System.out.println(result);
//		}
//
//		//clear
//		if (zkClient.checkExists().forPath(path) != null) {
//			zkClient.delete().deletingChildrenIfNeeded().forPath(path);
//		}
		
//		//close
//		CloseableUtils.closeQuietly(cache);
//		if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
//			CloseableUtils.closeQuietly(zkClient);
//		}
	}

}
