package com.sample.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * MyHttpServerHandler
 *
 * @author Aaric, created on 2020-04-27T19:33.
 * @version 1.1.0-SNAPSHOT
 */
@Slf4j
public class MyHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel registered...");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel active...");
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            log.info("http method name: {}", request.method().name());

            if ("/favicon.ico".equals(request.uri())) {
                log.info("http uri path: {}", request.uri());
                return;
            }

            ByteBuf content = Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8);

            HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, content);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel inactive");
        super.channelInactive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel unregistered...");
        super.channelUnregistered(ctx);
    }
}
