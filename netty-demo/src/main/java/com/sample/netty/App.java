package com.sample.netty;

import com.sample.netty.server.TcpServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Launcher.
 *
 * @author Aaric, created on 2018-04-28T16:37.
 * @version 0.0.1-SNAPSHOT
 */
@SpringBootApplication
public class App implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Value("${netty.serverPort}")
    private int serverPort;

    @Override
    public void run(String... args) throws Exception {
        Thread serverThread = new Thread(new TcpServer(serverPort));
        serverThread.start();
    }
}
