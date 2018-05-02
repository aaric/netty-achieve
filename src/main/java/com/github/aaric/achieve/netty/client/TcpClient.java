package com.github.aaric.achieve.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * TcpClient
 *
 * @author Aaric, created on 2018-05-02T10:19.
 * @since 0.1.0-SNAPSHOT
 */
public class TcpClient {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TcpClient.class);

    /**
     * Main
     *
     * @param args
     */
    public static void main(String[] args) {
        // worker负责读写数据
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            // 辅助启动类
            Bootstrap bootstrap = new Bootstrap();

            // 设置线程池
            bootstrap.group(worker);

            // 设置socket工厂
            bootstrap.channel(NioSocketChannel.class);

            // 设置管道
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    // 获取管道
                    ChannelPipeline pipeline = socketChannel.pipeline();

                    // 字符串解码器
                    pipeline.addLast(new StringDecoder());

                    // 字符串编码器
                    pipeline.addLast(new StringEncoder());

                    // 处理类
                    pipeline.addLast(new TcpClientChannelHandler());
                }
            });

            // 发起异步连接操作
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 7777)).sync();

            // 等待客户端链路关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("main, {}", e);
        }

    }

    /**
     * TcpClientChannelHandler
     */
    private static class TcpClientChannelHandler extends SimpleChannelInboundHandler<String> {

        /**
         * 接受服务端发来的数据
         *
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            // 打印服务端接收数据
            logger.info("Server: {}", msg);
        }

        /**
         * 与服务器建立连接
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // 给服务器发消息
            ctx.channel().writeAndFlush("I'm client!");

            // 打印连接信息
            logger.info("{} connected.", ctx.channel().remoteAddress());
        }

        /**
         * 与服务器断开连接
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
}
