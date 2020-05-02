package com.sample.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;

/**
 * com.sample.netty.client
 *
 * @author Aaric, created on 2020-05-02T14:52.
 * @version 0.1.0-SNAPSHOT
 */
@Slf4j
public class MySocketClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(UUID.randomUUID().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("remoteAddress: {}", ctx.channel().remoteAddress());
        log.info("client output: {}", msg);

        ctx.writeAndFlush("from client: " + Instant.now().toEpochMilli());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught", cause);
        ctx.close();
    }
}
