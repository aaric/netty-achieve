package com.sample.netty.runner;

import com.sample.netty.server.HttpServerChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Netty服务启动类
 *
 * @author Aaric, created on 2020-04-27T19:15.
 * @version 1.1.0-SNAPSHOT
 */
@Slf4j
@Order(1)
@Component
public class NettyServerRunner implements CommandLineRunner {

    @Value("${netty.serverPort}")
    private int serverPort;

    @Override
    public void run(String... args) throws Exception {
        // 启动服务
        log.info("starting...");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpServerChannel());

        ChannelFuture channelFuture = serverBootstrap.bind(serverPort).sync();
        channelFuture.channel().closeFuture().sync();

        // 关闭服务
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("stopping...");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("stopped.");
        }));

        // 打印日志
        log.info("started.");
    }
}
