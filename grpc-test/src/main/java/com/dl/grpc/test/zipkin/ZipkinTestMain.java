package com.dl.grpc.test.zipkin;

import java.util.Iterator;

import com.dl.grpc.test.kv.KvClient;
import com.dl.grpc.test.kv.KvServer;
import com.dl.grpc.test.user.UserClient;
import com.dl.grpc.test.user.UserServer;
import com.google.common.collect.Iterators;

public class ZipkinTestMain {
	public static void main(String[] args) throws Exception {

		KvClient kvClient = new KvClient("localhost", 50051);
		
		KvServer kvServer = new KvServer();
		kvServer.start();
		
		UserServer userServer = new UserServer(kvClient);
		userServer.start();
		
		UserClient userClient = new UserClient("localhost", 50052);
        Iterator<String> users = Iterators.cycle("karen", "bob", "john");
        int i = 0;
        while (users.hasNext() && i < 100) {
			userClient.getUser(users.next());
	        i++;
        }
			
		kvClient.shutdown();
		userClient.shutdown();
		
		kvServer.stop();
		userServer.stop();
	}

}
