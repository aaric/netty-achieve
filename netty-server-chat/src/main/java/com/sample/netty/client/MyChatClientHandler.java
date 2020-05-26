package com.sample.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * MyChatClientHandler
 *
 * @author Aaric, created on 2020-05-18T10:24.
 * @version 1.4.0-SNAPSHOT
 */
public class MyChatClientHandler extends SimpleChannelInboundHandler<String> {
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

    }
}
