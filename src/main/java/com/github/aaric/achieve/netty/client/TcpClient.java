package com.github.aaric.achieve.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * TcpClient
 *
 * @author Aaric, created on 2018-05-02T10:19.
 * @since 0.1.0-SNAPSHOT
 */
public class TcpClient implements Runnable {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TcpClient.class);

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

        try {
            // 辅助启动类
            Bootstrap bootstrap = new Bootstrap();

            // 设置线程池
            bootstrap.group(workerGroup);

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

            // 设置TCP参数
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true); //维持连接的活跃，清除死连接
            bootstrap.option(ChannelOption.TCP_NODELAY, true); //关闭延迟发送

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
            logger.error("main, {}", e);
        } finally {
            // 优雅退出
            workerGroup.shutdownGracefully();
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

    /**
     * Main
     *
     * @param args
     */
    public static void main(String[] args) {
        // 初始化客户端数目
        int clientCount = 2000;

        // 创建客户端连接
        Thread clientThread = new Thread(new TcpClient("127.0.0.1", 7777, clientCount));
        clientThread.start();

        // 定时发送数据
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {

            // 打印当前已经建立连接总数
            logger.info("Current Client Total: {}", clientMap.size());

            // 向服务端数据
            if (clientCount == clientMap.size()) {
                // 循环发送数据
                for (Map.Entry<String, Channel> channelEntry : clientMap.entrySet()) {
                    channelEntry.getValue()
                            .writeAndFlush(Unpooled.copiedBuffer(channelEntry.getKey().getBytes()))
                            .addListener(future -> {
                                if (!future.isSuccess()) {
                                    logger.error("{} send failure!", channelEntry.getKey());
                                }
                            });
                }
            }
        }, 3, 1, TimeUnit.SECONDS);
    }
}
