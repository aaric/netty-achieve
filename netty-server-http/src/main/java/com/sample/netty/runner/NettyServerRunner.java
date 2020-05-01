package com.sample.netty.runner;

import com.sample.netty.server.MyHttpServer;
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
        MyHttpServer httpServer = new MyHttpServer(serverPort);
        httpServer.start();

        // 关闭服务
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            httpServer.stop();
        }));
    }
}
