package com.incarcloud.boar.runner;

import com.incarcloud.boar.config.SlotsConfig;
import com.incarcloud.boar.gather.GatherHost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 网关启动类
 *
 * @author Aaric, created on 2020-05-07T16:11.
 * @version 1.3.0-SNAPSHOT
 */
@Slf4j
@Order(1)
@Component
public class GatewayRunner implements CommandLineRunner {

    @Autowired
    private SlotsConfig slotsConfig;

    @Autowired
    private GatherHost gatherHost;

    @Override
    public void run(String... args) throws Exception {
        /**
         * 启动网关服务
         */
        gatherHost.addSlot(slotsConfig.getSlots());
        gatherHost.start();

        /**
         * 关闭网关服务
         */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            gatherHost.stop();
        }));
    }
}
