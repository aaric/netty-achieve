package com.sample.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.log4j.Log4j2;

/**
 * TcpServer
 *
 * @author Aaric, created on 2018-05-02T10:19.
 * @version 0.1.0-SNAPSHOT
 */
@Log4j2
public class TcpServer implements Runnable {
    
    /**
     * 绑定端口
     */
    private int serverPort;

    /**
     * 构造函数
     *
     * @param serverPort 绑定端口
     */
    public TcpServer(int serverPort) {
        this.serverPort = serverPort;
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

            // 设置socket工厂，并设置TCP参数
            bootstrap.channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024) //连接缓冲池的大小
                    .option(ChannelOption.SO_KEEPALIVE, true) //维持连接的活跃，清除死连接
                    .option(ChannelOption.TCP_NODELAY, true); //关闭延迟发送

            // 设置管道工厂
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    // 获取管道
                    ChannelPipeline pipeline = socketChannel.pipeline();

                    // 处理类
                    pipeline.addLast(new TcpServerChannelHandler());
                }
            });

            // 绑定端口
            ChannelFuture future = bootstrap.bind(serverPort).sync();
            log.info("Server start.");

            // 等待服务端监听端口关闭
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("main, {}", e);
        } finally {
            // 优雅退出
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * TcpServerHandler
     */
    private static class TcpServerChannelHandler extends ChannelInboundHandlerAdapter {

        /**
         * 读取客户端发送的数据
         *
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            try {
                // 打印服务端接收数据
                ByteBuf buffer = (ByteBuf) msg;
                log.info("Client: {}", new String(ByteBufUtil.getBytes(buffer)));

                // 回复客户端数据
                ctx.channel().writeAndFlush(Unpooled.wrappedBuffer("I'm server!".getBytes()));

            } finally {
                ReferenceCountUtil.release(msg);
            }
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
            log.info("{} connected.", ctx.channel().remoteAddress());
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
            log.info("{} disconnected.", ctx.channel().remoteAddress());
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
            /*log.error("exceptionCaught, {}", cause);*/
        }
    }

    /**
     * Main
     *
     * @param args
     */
    public static void main(String[] args) {
        // 创建服务端
        Thread serverThread = new Thread(new TcpServer(7777));
        serverThread.start();
    }
}
