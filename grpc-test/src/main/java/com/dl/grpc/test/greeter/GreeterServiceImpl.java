package com.dl.grpc.test.greeter;

import com.dl.grpc.test.proto.GreeterServiceGrpc.GreeterServiceImplBase;
import com.dl.grpc.test.proto.GreeterServiceProto.HelloReply;
import com.dl.grpc.test.proto.GreeterServiceProto.HelloRequest;

import io.grpc.stub.StreamObserver;

public class GreeterServiceImpl extends GreeterServiceImplBase{
	@Override
	public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
		HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}

	@Override
	public void sayHelloAgain(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
		HelloReply reply = HelloReply.newBuilder().setMessage("Hello again " + req.getName()).build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}
}
