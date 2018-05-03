package com.github.aaric.achieve.netty;

import com.github.aaric.achieve.netty.server.TcpServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Launcher.
 *
 * @author Aaric, created on 2018-04-28T16:37.
 * @since 0.0.1-SNAPSHOT
 */
@SpringBootApplication
public class App implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Thread serverThread = new Thread(new TcpServer(7777));
        serverThread.start();
    }
}
