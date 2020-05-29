package com.incarcloud.boar.gather;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 操作对象
 *
 * @author Aaric, created on 2020-05-29T09:23.
 * @version 1.4.0-SNAPSHOT
 */
public interface GatherOperator {

    /**
     * 获取redis操作
     *
     * @return
     */
    RedisTemplate<String, String> opsForRedis();

    /**
     * 获取kafka操作
     *
     * @return
     */
    KafkaTemplate<String, String> opsForKafka();
}
