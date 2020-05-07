package com.incarcloud.boar.gather;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 支持UDP协议的采集槽
 *
 * @author Aaric, created on 2020-05-07T18:36.
 * @version 1.3.0-SNAPSHOT
 */
@Slf4j
public class GatherUDPSlot extends GatherSlot {

    /**
     * 支持UDP协议名称
     */
    public static final String SUPPORT_PROTOCOL = "udp";

    /**
     * 指定SO_BACKLOG最大值
     */
    private static final int BACKLOG_COUNT = 1024;

    /**
     * Netty对象
     */
    private int port;
    private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;

    protected GatherUDPSlot(GatherHost host, int port) {
        super(host);
        this.port = port;
        GatherSlot slot = this;

        this.workerGroup = new NioEventLoopGroup();

        this.bootstrap = new Bootstrap()
                .group(this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new GatherChannelHandler(slot));
                    }
                });

        this.bootstrap.option(ChannelOption.SO_BACKLOG, BACKLOG_COUNT);
    }

    @Override
    public void startup() throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.bind(this.port).sync();
        channelFuture.channel().closeFuture().sync();
    }

    @Override
    public void shutdown() {
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
