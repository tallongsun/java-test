package com.dl.grpc.test.greeter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

public class GreeterServer {
	private static final Logger logger = Logger.getLogger(GreeterServer.class.getName());
	private Server server;

	public static void main(String[] args) throws IOException, InterruptedException {
		final GreeterServer server = new GreeterServer();
		server.start();
		server.blockUntilShutdown();
	}

	private void start() throws IOException {
		int port = 50051;
		server = ServerBuilder.forPort(port).executor(Executors.newFixedThreadPool(1)).addService(
				ServerInterceptors.intercept(new GreeterServiceImpl(), new HeaderServerInterceptor())).
				build().start();
		logger.info("Server started, listening on " + port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.err.println("*** shutting down gRPC server since JVM is shutting down");
				GreeterServer.this.stop();
				System.err.println("*** server shut down");
			}
		});
	}

	private void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

}
