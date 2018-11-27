package com.dl.grpc.test.user;

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
import services.UserRequest;
import services.UserResponse;
import services.UserServiceGrpc;
import services.UserServiceGrpc.UserServiceBlockingStub;

public class UserClient {
	private static final Logger logger = Logger.getLogger(GreeterClient.class.getName());

	private final ManagedChannel channel;
	private final UserServiceBlockingStub blockingStub;

	public UserClient(String host, int port) {
		this(ManagedChannelBuilder.forAddress(host, port)
				.usePlaintext(true));
	}

	UserClient(ManagedChannelBuilder<?> channelBuilder) {
		channel = channelBuilder.build();
	    ClientInterceptor interceptor = new BraveGrpcClientInterceptor(BraveUtil.brave("userClient"));
	    Channel newChannel = ClientInterceptors.intercept(channel, interceptor);
		blockingStub = UserServiceGrpc.newBlockingStub(newChannel);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	public void getUser(String name) {
		logger.info("Will try to greet " + name + " ...");
		UserRequest request = UserRequest.newBuilder().setName(name).build();
		UserResponse response;
		try {
			response = blockingStub.getUser(request);
		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
			return;
		}
		logger.info("Greeting: " + response);
	}

	public static void main(String[] args) throws Exception {
		UserClient client = new UserClient("localhost", 50052);
		try {
			String user = "karen";
			client.getUser(user);
		} finally {
			client.shutdown();
		}
	}
}
