package com.sample.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * com.sample.netty.client
 *
 * @author Aaric, created on 2020-05-02T14:46.
 * @version 1.2.0-SNAPSHOT
 */
public class MySocketClient {

    private String hostname;
    private int port;

    public MySocketClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void connect() throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new MySocketClientChannel());

            ChannelFuture channelFuture = bootstrap.connect("localhost", 8888)
                    .sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
