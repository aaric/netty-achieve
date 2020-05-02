package com.sample.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * MySocketServer
 *
 * @author Aaric, created on 2020-05-01T20:45.
 * @version 1.2.0-SNAPSHOT
 */
@Slf4j
public class MySocketServer {

    private int serverPort;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;

    public MySocketServer(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * 启动服务
     */
    public void start() throws InterruptedException {
        log.info("starting...");
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new MySocketServerChannel());

        ChannelFuture channelFuture = serverBootstrap.bind(serverPort).sync();
        channelFuture.channel().closeFuture().sync();

        log.info("started.");
    }

    public void stop() {
        log.info("stopping...");

        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();

        log.info("stopped.");
    }
}
