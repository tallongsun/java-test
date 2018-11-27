package com.dl.grpc.test.greeter;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dl.grpc.test.proto.GreeterServiceGrpc;
import com.dl.grpc.test.proto.GreeterServiceGrpc.GreeterServiceBlockingStub;
import com.dl.grpc.test.proto.GreeterServiceProto.HelloReply;
import com.dl.grpc.test.proto.GreeterServiceProto.HelloRequest;

import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class GreeterClient {
	private static final Logger logger = Logger.getLogger(GreeterClient.class.getName());

	private final ManagedChannel channel;
	private final GreeterServiceBlockingStub blockingStub;
//	private final GreeterServiceJsonStub blockingStub;

	public GreeterClient(String host, int port) {
		this(ManagedChannelBuilder.forAddress(host, port)
				.usePlaintext(true));
	}

	GreeterClient(ManagedChannelBuilder<?> channelBuilder) {
		channel = channelBuilder.build();
	    ClientInterceptor interceptor = new HeaderClientInterceptor();
	    Channel newChannel = ClientInterceptors.intercept(channel, interceptor);
		blockingStub = GreeterServiceGrpc.newBlockingStub(newChannel);
//		blockingStub = new GreeterServiceJsonStub(newChannel);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	public void greet(String name) {
		logger.info("Will try to greet " + name + " ...");
		HelloRequest request = HelloRequest.newBuilder().setName(name).build();
		HelloReply response;
		try {
			response = blockingStub.sayHello(request);
		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
			return;
		}
		logger.info("Greeting: " + response.getMessage());
//		try {
//			response = blockingStub.sayHelloAgain(request);
//		} catch (StatusRuntimeException e) {
//			logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
//			return;
//		}
//		logger.info("Greeting: " + response.getMessage());
	}

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 2; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					GreeterClient client = new GreeterClient("localhost", 50051);
					try {
						String user = "world";
						if (args.length > 0) {
							user = args[0];
						}

						client.greet(user);
					} finally {
						try {
							client.shutdown();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}
			}).start();
		}
		
		Thread.sleep(5000);

	}
}
