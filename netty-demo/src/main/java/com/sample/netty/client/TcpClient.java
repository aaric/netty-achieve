package com.sample.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * TcpClient
 *
 * @author Aaric, created on 2018-05-02T10:19.
 * @version 0.1.0-SNAPSHOT
 */
@Log4j2
public class TcpClient implements Runnable {

    /**
     * 缓存客户端连接
     */
    private static Map<String, Channel> clientMap = new ConcurrentHashMap<>();

    /**
     * 连接服务主机地址
     */
    private String serverHost;

    /**
     * 连接服务端口
     */
    private int serverPort;

    /**
     * 创建最大客户端总数
     */
    private int initClientTotal;

    /**
     * 构造函数
     *
     * @param serverHost      连接服务主机地址
     * @param serverPort      连接服务端口
     * @param initClientTotal 创建最大客户端总数
     */
    public TcpClient(String serverHost, int serverPort, int initClientTotal) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.initClientTotal = initClientTotal;
    }

    @Override
    public void run() {
        // worker负责读写数据
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        // 定时发送数据
        workerGroup.scheduleAtFixedRate(() -> {

            // 打印当前已经建立连接总数
            log.info("Current Client Total: {}", clientMap.size());

            // 向服务端数据
            if (this.initClientTotal == clientMap.size()) {
                // 循环发送数据
                for (Map.Entry<String, Channel> channelEntry : clientMap.entrySet()) {
                    channelEntry.getValue().writeAndFlush(Unpooled.wrappedBuffer(channelEntry.getKey().getBytes()));
                }
            }
        }, 3, 2, TimeUnit.SECONDS);

        try {
            // 辅助启动类
            Bootstrap bootstrap = new Bootstrap();

            // 设置线程池
            bootstrap.group(workerGroup);

            // 设置TCP参数
            bootstrap.channel(NioSocketChannel.class) // 设置socket工厂
                    /*.option(ChannelOption.SO_KEEPALIVE, true) //维持连接的活跃，清除死连接
                    .option(ChannelOption.TCP_NODELAY, true)*/ //关闭延迟发送
                    .handler(new ChannelInitializer<SocketChannel>() { // 设置管道

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 获取管道
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            // 处理类
                            pipeline.addLast(new TcpClientChannelHandler());
                        }
                    });

            // 发起异步连接操作
            ChannelFuture future;
            for (int i = 0; i < initClientTotal; i++) {
                // 发起异步连接操作
                future = bootstrap.connect(serverHost, serverPort).sync();

                // 缓存客户端连接
                if (future.isSuccess()) {
                    clientMap.put(future.channel().id().asLongText(), future.channel());
                }
            }

            // 等待客户端链路关闭
            for (Map.Entry<String, Channel> channelEntry : clientMap.entrySet()) {
                channelEntry.getValue().closeFuture().sync();
            }

        } catch (Exception e) {
            log.error("main, {}", e);
        } finally {
            // 优雅退出
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * TcpClientChannelHandler
     */
    private static class TcpClientChannelHandler extends ChannelInboundHandlerAdapter {

        /**
         * 接受服务端发来的数据
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
                log.info("Server: {}", new String(ByteBufUtil.getBytes(buffer)));
            } finally {
                ReferenceCountUtil.release(msg);
            }
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
            log.info("{} connected.", ctx.channel().remoteAddress());
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
            log.error("exceptionCaught, {}", cause);
        }
    }

    /**
     * Main
     *
     * @param args
     */
    public static void main(String[] args) {
        // 创建客户端连接
        Thread clientThread = new Thread(new TcpClient("127.0.0.1", 7777, 2000));
        clientThread.start();
    }
}
