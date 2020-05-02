package com.sample.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * MySocketServerHandler
 *
 * @author Aaric, created on 2020-05-02T14:29.
 * @version 1.2.0-SNAPSHOT
 */
@Slf4j
public class MySocketServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("remoteAddress: {}, msg: {}", ctx.channel().remoteAddress(), msg);

        ctx.writeAndFlush("from server: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught", cause);
        ctx.close();
    }
}
