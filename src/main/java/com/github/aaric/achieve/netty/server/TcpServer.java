package com.github.aaric.achieve.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TcpServer
 *
 * @author Aaric, created on 2018-05-02T10:19.
 * @since 0.1.0-SNAPSHOT
 */
public class TcpServer implements Runnable {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

    /**
     * 服务绑定端口
     */
    private int serverPort;

    /**
     * 最大连接数
     */
    private int maxClient;

    /**
     * 构造函数
     *
     * @param serverPort 服务绑定端口
     * @param maxClient  最大连接数
     */
    public TcpServer(int serverPort, int maxClient) {
        this.serverPort = serverPort;
        this.maxClient = maxClient;
    }

    @Override
    public void run() {
        // boss线程监听端口，worker线程负责数据读写
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 辅助启动类
            ServerBootstrap bootstrap = new ServerBootstrap();

            // 设置线程池
            bootstrap.group(bossGroup, workerGroup);

            // 设置socket工厂
            bootstrap.channel(NioServerSocketChannel.class);

            // 设置管道工厂
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    // 获取管道
                    ChannelPipeline pipeline = socketChannel.pipeline();

                    // 字符串解码器
                    pipeline.addLast(new StringDecoder());

                    // 字符串编码器
                    pipeline.addLast(new StringEncoder());

                    // 处理类
                    pipeline.addLast(new TcpServerChannelHandler());
                }
            });

            // 设置TCP参数
            bootstrap.option(ChannelOption.SO_BACKLOG, maxClient); //连接缓冲池的大小
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true); //维持连接的活跃，清除死连接
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true); //关闭延迟发送

            // 绑定端口
            ChannelFuture future = bootstrap.bind(serverPort).sync();
            logger.info("Server start.");

            // 等待服务端监听端口关闭
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            logger.error("main, {}", e);
        } finally {
            // 优雅退出
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * TcpServerHandler
     */
    private static class TcpServerChannelHandler extends SimpleChannelInboundHandler<String> {

        /**
         * 读取客户端发送的数据
         *
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            // 打印服务端接收数据
            logger.info("Client: {}", msg);

            // 回复客户端数据
            ctx.channel().writeAndFlush("I'm server!");
        }

        /**
         * 新客户端接入
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // 打印连接信息
            logger.info("{} connected.", ctx.channel().remoteAddress());
        }

        /**
         * 客户端断开
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            // 打印断开信息
            logger.info("{} disconnected.", ctx.channel().remoteAddress());
        }

        /**
         * 异常
         *
         * @param ctx
         * @param cause
         * @throws Exception
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            // 关闭通道
            ctx.channel().close();

            // 打印异常
            logger.error("exceptionCaught, {}", cause);
        }
    }

    /**
     * Main
     *
     * @param args
     */
    public static void main(String[] args) {
        TcpServer server = new TcpServer(7777, 1024 * 2 * 100);
        server.run();
    }
}
