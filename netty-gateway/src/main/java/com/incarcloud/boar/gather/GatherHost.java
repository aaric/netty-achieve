package com.incarcloud.boar.gather;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 采集槽主机
 *
 * @author Aaric, created on 2020-05-07T16:38.
 * @version 1.3.0-SNAPSHOT
 */
@Slf4j
public class GatherHost {

    /**
     * 主机名称
     */
    private String name;

    /**
     * 是否已启动
     */
    private boolean bRunning = false;

    /**
     * 采集槽列表
     */
    private ArrayList<GatherSlot> slots = new ArrayList<>();

    public GatherHost() {
        this(String.format("host-%d", Instant.now().toEpochMilli()));
    }

    public GatherHost(String name) {
        this.name = name;
    }

    /**
     * 添加采集槽
     *
     * @param slotCfgList 采集槽配置
     * @throws Exception
     */
    public void addSlot(List<String> slotCfgList) throws Exception {
        if (null == slotCfgList || 0 == slotCfgList.size()) {
            throw new RuntimeException("No slot cfg!!!");
        }

        // 构建采集槽
        for (String slotCfg : slotCfgList) {
            // slot格式 -> 解析器:通信协议:通信端口
            String parser = slotCfg.split(":")[0];
            String protocol = slotCfg.split(":")[1];
            String port = slotCfg.split(":")[2];

            // 初始化采集槽列表
            GatherSlot gatherSlot;
            switch (protocol.toLowerCase()) {
                case GatherTCPSlot.SUPPORT_PROTOCOL:
                    // TCP
                    gatherSlot = new GatherTCPSlot(this, Integer.parseInt(port));
                    gatherSlot.setDataParser(parser);
                    slots.add(gatherSlot);
                    break;
                case GatherUDPSlot.SUPPORT_PROTOCOL:
                    gatherSlot = new GatherUDPSlot(this, Integer.parseInt(port));
                    gatherSlot.setDataParser(parser);
                    slots.add(gatherSlot);
                    // UDP
                    break;
                default:
                    // MQTT
                    throw new UnsupportedOperationException(parser);
            }
        }
    }

    /**
     * 启动服务
     *
     * @throws Exception
     */
    public void start() throws Exception {
        log.info("host starting...");

        // 忽略已启动
        if (bRunning) {
            return;
        }

        // 判断采集槽是否为空
        if (null == slots || 0 == slots.size()) {
            throw new RuntimeException("No slot!!!");
        }

        // 启动所有采集槽
        for (GatherSlot slot : slots) {
            slot.startup();
        }

        // 已启动
        bRunning = true;
    }

    public void stop() {
        log.info("host stopping...");

        // 关闭所有采集槽
        for (GatherSlot slot : slots) {
            slot.shutdown();
        }

        // 已关闭
        bRunning = true;

        log.info("stopped.");
    }

    public String getName() {
        return name;
    }
}
