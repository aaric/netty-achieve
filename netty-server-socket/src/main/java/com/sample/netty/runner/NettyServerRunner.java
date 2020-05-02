package com.sample.netty.runner;

import com.sample.netty.server.MySocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Netty服务启动类
 *
 * @author Aaric, created on 2020-05-01T20:44.
 * @version 1.2.0-SNAPSHOT
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
        MySocketServer socketServer = new MySocketServer(serverPort);
        socketServer.start();

        // 关闭服务
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            socketServer.stop();
        }));
    }
}
