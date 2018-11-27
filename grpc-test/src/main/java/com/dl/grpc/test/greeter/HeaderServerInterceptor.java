package com.dl.grpc.test.greeter;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

import java.util.logging.Logger;

public class HeaderServerInterceptor implements ServerInterceptor{
	  private static final Logger logger = Logger.getLogger(HeaderServerInterceptor.class.getName());

	  @VisibleForTesting
	  static final Metadata.Key<String> CUSTOM_HEADER_KEY =
	      Metadata.Key.of("custom_server_header_key", Metadata.ASCII_STRING_MARSHALLER);
	@Override
	public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata requestHeaders,
			ServerCallHandler<ReqT, RespT> next) {
	    logger.info("header received from client:" + requestHeaders);
	    return next.startCall(new SimpleForwardingServerCall<ReqT, RespT>(call) {
	      @Override
	      public void sendHeaders(Metadata responseHeaders) {
	    	  logger.info("sendHeaders");
	        responseHeaders.put(CUSTOM_HEADER_KEY, "customRespondValue");
	        super.sendHeaders(responseHeaders);
	      }

		@Override
		public void close(Status status, Metadata trailers) {
			logger.info("close");
			super.close(status, trailers);
		}
	      
	    }, requestHeaders);
	}

}
