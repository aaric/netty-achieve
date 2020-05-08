package com.sample.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * MyHttpServer
 *
 * @author Aaric, created on 2020-04-30T19:46.
 * @version 1.2.0-SNAPSHOT
 */
@Slf4j
public class MyHttpServer {

    private int serverPort;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public MyHttpServer(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * 启动服务
     */
    public void start() throws InterruptedException {
        log.info("starting...");

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new MyHttpServerChannel());

        ChannelFuture channelFuture = serverBootstrap.bind(serverPort).sync();
        channelFuture.channel().closeFuture().sync(); //阻塞后面代码执行

        log.info("started.");
    }

    public void stop() {
        log.info("stopping...");

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        log.info("stopped.");
    }
}
