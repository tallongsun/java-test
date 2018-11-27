package com.dl.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;

    public HttpServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        //ssl
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        //codec
        p.addLast(new HttpResponseEncoder());
        p.addLast(new HttpRequestDecoder());
//        //chunk
//        p.addLast(new HttpObjectAggregator(65536));
//        p.addLast(new ChunkedWriteHandler());
//        //compress
//        p.addLast(new HttpContentCompressor());
//        //cors
//        p.addLast(new CorsHandler(CorsConfigBuilder.forAnyOrigin().build()));
        //dispatch
        p.addLast(new HttpServerHandler());
    }
}
