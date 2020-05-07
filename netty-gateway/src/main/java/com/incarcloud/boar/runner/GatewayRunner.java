package com.incarcloud.boar.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 网关启动类
 *
 * @author Aaric, created on 2020-05-07T16:11.
 * @version 1.5.0-SNAPSHOT
 */
@Slf4j
@Order(1)
@Component
public class GatewayRunner implements CommandLineRunner {

    @Value("${spring.kafka.topic.tbox}")
    private String topicTBox;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.debug("send msg...");
        kafkaTemplate.send(topicTBox, "hello world");
    }
}
