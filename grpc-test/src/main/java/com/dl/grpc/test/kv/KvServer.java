package com.dl.grpc.test.kv;

import java.io.IOException;
import java.util.logging.Logger;

import com.dl.grpc.test.greeter.GreeterServer;
import com.dl.grpc.test.greeter.GreeterServiceImpl;
import com.dl.grpc.test.greeter.HeaderServerInterceptor;
import com.dl.grpc.test.zipkin.BraveUtil;
import com.github.kristofa.brave.grpc.BraveGrpcServerInterceptor;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

public class KvServer {
	private static final Logger logger = Logger.getLogger(GreeterServer.class.getName());
	private Server server;

	public static void main(String[] args) throws IOException, InterruptedException {
		final KvServer server = new KvServer();
		server.start();
		server.blockUntilShutdown();
	}

	public void start() throws IOException {
		int port = 50051;
		server = ServerBuilder.forPort(port).addService(
				ServerInterceptors.intercept(new KvServiceImpl(), new BraveGrpcServerInterceptor(BraveUtil.brave("kvService")))).
				build().start();
		logger.info("Server started, listening on " + port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.err.println("*** shutting down gRPC server since JVM is shutting down");
				KvServer.this.stop();
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
