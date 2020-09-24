package com.incarcloud.boar.gather;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 支持TCP协议的采集槽
 *
 * @author Aaric, created on 2020-05-07T18:36.
 * @version 1.3.0-SNAPSHOT
 */
@Slf4j
public class GatherTCPSlot extends GatherSlot {

    /**
     * 支持TCP协议名称
     */
    public static final String SUPPORT_PROTOCOL = "tcp";

    /**
     * 指定SO_BACKLOG最大值
     */
    private static final int BACKLOG_COUNT = 1024 * 200;

    /**
     * Netty对象
     */
    private int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;

    protected GatherTCPSlot(GatherHost host, int port) {
        super(host);
        this.port = port;
        GatherSlot slot = this;

        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

        this.serverBootstrap = new ServerBootstrap()
                .group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("LoggingHandler", new LoggingHandler(LogLevel.INFO));
                        ch.pipeline().addLast("IdleStateHandler", new IdleStateHandler(30L, 0L, 0L, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new GatherChannelHandler(slot));
                    }
                });

        this.serverBootstrap.option(ChannelOption.SO_BACKLOG, BACKLOG_COUNT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);
    }

    @Override
    public void startup() throws InterruptedException {
        ChannelFuture channelFuture = serverBootstrap.bind(this.port).sync();
        // closeFuture会阻塞后面的代码，开启一个新线程去处理阻塞逻辑
        new Thread(() -> {
            try {
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // 打印日志
        log.info("{} started.", this.getName());
    }

    @Override
    public void shutdown() {
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }

    @Override
    public String getTransportProtocol() {
        return SUPPORT_PROTOCOL;
    }

    @Override
    public int getListenPort() {
        return this.port;
    }
}
