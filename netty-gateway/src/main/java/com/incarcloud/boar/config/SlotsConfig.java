package com.incarcloud.boar.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 采集槽配置
 *
 * @author Aaric, created on 2020-05-08T09:38.
 * @version 1.3.0-SNAPSHOT
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "incarcloud.host")
public class SlotsConfig {

    private List<String> slots;
}
