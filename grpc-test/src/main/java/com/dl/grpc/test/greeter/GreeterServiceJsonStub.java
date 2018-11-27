package com.dl.grpc.test.greeter;

import com.dl.grpc.test.proto.GreeterServiceGrpc;
import com.dl.grpc.test.proto.GreeterServiceProto.HelloReply;
import com.dl.grpc.test.proto.GreeterServiceProto.HelloRequest;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.ClientCalls;

public class GreeterServiceJsonStub  extends AbstractStub<GreeterServiceJsonStub> {
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

        protected GreeterServiceJsonStub(Channel channel) {
          super(channel);
        }

        protected GreeterServiceJsonStub(Channel channel, CallOptions callOptions) {
          super(channel, callOptions);
        }

        @Override
        protected GreeterServiceJsonStub build(Channel channel, CallOptions callOptions) {
          return new GreeterServiceJsonStub(channel, callOptions);
        }

        public HelloReply sayHello(HelloRequest request) {
          return ClientCalls.blockingUnaryCall(
              getChannel(), METHOD_SAY_HELLO, getCallOptions(), request);
        }
        
        public HelloReply sayHelloAgain(HelloRequest request) {
            return ClientCalls.blockingUnaryCall(
                getChannel(), METHOD_SAY_HELLO_AGAIN, getCallOptions(), request);
          }

}
