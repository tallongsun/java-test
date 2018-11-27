package com.dl.grpc.test.kv;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dl.grpc.test.greeter.GreeterClient;
import com.dl.grpc.test.zipkin.BraveUtil;
import com.github.kristofa.brave.grpc.BraveGrpcClientInterceptor;

import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import services.GetRequest;
import services.GetResponse;
import services.KeyValueServiceGrpc;
import services.KeyValueServiceGrpc.KeyValueServiceBlockingStub;

public class KvClient {
	private static final Logger logger = Logger.getLogger(GreeterClient.class.getName());

	private final ManagedChannel channel;
	private final KeyValueServiceBlockingStub blockingStub;

	public KvClient(String host, int port) {
		this(ManagedChannelBuilder.forAddress(host, port)
				.usePlaintext(true));
	}
	
	public KeyValueServiceBlockingStub getStub(){
		return blockingStub;
	}

	KvClient(ManagedChannelBuilder<?> channelBuilder) {
		channel = channelBuilder.build();
	    ClientInterceptor interceptor = new BraveGrpcClientInterceptor(BraveUtil.brave("kvClient"));
	    Channel newChannel = ClientInterceptors.intercept(channel, interceptor);
		blockingStub = KeyValueServiceGrpc.newBlockingStub(newChannel);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	public void get(String name) {
		logger.info("Will try to greet " + name + " ...");
		GetRequest request = GetRequest.newBuilder().setKey(name).build();
		GetResponse response;
		try {
			response = blockingStub.get(request);
		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
			return;
		}
		logger.info("Greeting: " + response.getValue());
	}

	public static void main(String[] args) throws Exception {
		KvClient client = new KvClient("localhost", 50051);
		try {
			String user = "world";
			client.get(user);
		} finally {
			client.shutdown();
		}
	}
}
