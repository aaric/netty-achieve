package com.incarcloud.boar.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * Kafka配置
 *
 * @author Aaric, created on 2020-05-06T18:31.
 * @version 1.3.0-SNAPSHOT
 */
@Slf4j
@Configuration
public class KafkaConfig {

    @KafkaListener(topics = "${spring.kafka.topic.tbox}")
    public void processTopicTBox(byte[] content) {
        // 打印日志
        log.debug("content: {}", content);

    }
}
