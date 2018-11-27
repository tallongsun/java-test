package com.dl.grpc.test.greeter;

import com.dl.grpc.test.proto.GreeterServiceGrpc;
import com.dl.grpc.test.proto.GreeterServiceProto.HelloReply;
import com.dl.grpc.test.proto.GreeterServiceProto.HelloRequest;

import io.grpc.BindableService;
import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.ServerCalls.UnaryMethod;
import io.grpc.stub.StreamObserver;

public class GreeterServiceJsonImpl implements BindableService{
	public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
		HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}

	public void sayHelloAgain(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
		HelloReply reply = HelloReply.newBuilder().setMessage("Hello again " + req.getName()).build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}
	
    static final MethodDescriptor<HelloRequest, HelloReply> METHOD_SAY_HELLO =
            MethodDescriptor.create(
                GreeterServiceGrpc.METHOD_SAY_HELLO.getType(),
                GreeterServiceGrpc.METHOD_SAY_HELLO.getFullMethodName(),
                ProtoUtils.jsonMarshaller(HelloRequest.getDefaultInstance()),
                ProtoUtils.jsonMarshaller(HelloReply.getDefaultInstance()));
    
    static final MethodDescriptor<HelloRequest, HelloReply> METHOD_SAY_HELLO_AGAIN =
            MethodDescriptor.create(
                GreeterServiceGrpc.METHOD_SAY_HELLO_AGAIN.getType(),
                GreeterServiceGrpc.METHOD_SAY_HELLO_AGAIN.getFullMethodName(),
                ProtoUtils.jsonMarshaller(HelloRequest.getDefaultInstance()),
                ProtoUtils.jsonMarshaller(HelloReply.getDefaultInstance()));
    
    @Override
    public ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition
          .builder(GreeterServiceGrpc.getServiceDescriptor().getName())
          .addMethod(METHOD_SAY_HELLO,
        		  ServerCalls.asyncUnaryCall(
                  new UnaryMethod<HelloRequest, HelloReply>() {
                    @Override
                    public void invoke(
                        HelloRequest request, StreamObserver<HelloReply> responseObserver) {
                      sayHello(request, responseObserver);
                    }
                  }))
          .addMethod(METHOD_SAY_HELLO_AGAIN,
        		  ServerCalls.asyncUnaryCall(
                  new UnaryMethod<HelloRequest, HelloReply>() {
                    @Override
                    public void invoke(
                        HelloRequest request, StreamObserver<HelloReply> responseObserver) {
                    	sayHelloAgain(request, responseObserver);
                    }
                  }))
          .build();
    }
}
