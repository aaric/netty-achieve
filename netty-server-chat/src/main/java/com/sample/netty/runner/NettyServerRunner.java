package com.sample.netty.runner;

import com.sample.netty.server.MyChatServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * NettyServerRunner
 *
 * @author Aaric, created on 2020-05-16T20:33.
 * @version 1.4.0-SNAPSHOT
 */
@Slf4j
@Order(1)
@Component
public class NettyServerRunner implements CommandLineRunner {

    @Value("${netty.serverPort}")
    private int serverPort;

    @Override
    public void run(String... args) throws Exception {
        MyChatServer chatServer = new MyChatServer(serverPort);
        chatServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            chatServer.stop();
        }));
    }
}
