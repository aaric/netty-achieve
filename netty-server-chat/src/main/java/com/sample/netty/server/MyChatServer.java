package com.sample.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * MyChatServer
 *
 * @author Aaric, created on 2020-05-16T20:32.
 * @version 1.4.0-SNAPSHOT
 */
@Slf4j
public class MyChatServer {

    private int serverPort;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public MyChatServer(int serverPort) {
        this.serverPort = serverPort;
    }

    public void start() throws InterruptedException {
        log.info("starting...");
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

                        pipeline.addLast(new MyChatServerHandler());
                    }
                });

        ChannelFuture channelFuture = serverBootstrap.bind(serverPort).sync();
        channelFuture.channel().closeFuture().sync();

        log.info("started.");
    }

    public void stop() {
        log.info("stopping...");

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        log.info("stopped.");
    }
}
