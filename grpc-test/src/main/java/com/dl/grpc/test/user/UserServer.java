package com.dl.grpc.test.user;

import java.io.IOException;
import java.util.logging.Logger;

import com.dl.grpc.test.greeter.GreeterServer;
import com.dl.grpc.test.kv.KvClient;
import com.dl.grpc.test.zipkin.BraveUtil;
import com.github.kristofa.brave.grpc.BraveGrpcServerInterceptor;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import services.KeyValueServiceGrpc.KeyValueServiceBlockingStub;

public class UserServer {
	private static final Logger logger = Logger.getLogger(GreeterServer.class.getName());
	private Server server;
	private KvClient kvClient;

	public static void main(String[] args) throws IOException, InterruptedException {
		final UserServer server = new UserServer(null);
		server.start();
		server.blockUntilShutdown();
	}
	

	public UserServer(KvClient kvClient){
		this.kvClient = kvClient;
	}

	public void start() throws IOException {
		int port = 50052;
		server = ServerBuilder.forPort(port).addService(
				ServerInterceptors.intercept(new UserServiceImpl(kvClient.getStub()), new BraveGrpcServerInterceptor(BraveUtil.brave("userService")))).
				build().start();
		logger.info("Server started, listening on " + port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.err.println("*** shutting down gRPC server since JVM is shutting down");
				UserServer.this.stop();
				System.err.println("*** server shut down");
			}
		});
	}

	public void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	public void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}
}
