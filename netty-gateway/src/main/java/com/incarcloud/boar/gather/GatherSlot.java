package com.incarcloud.boar.gather;

import com.incarcloud.boar.datapack.IDataParser;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

/**
 * 采集槽抽象类
 *
 * @author Aaric, created on 2020-05-07T17:14.
 * @version 1.3.0-SNAPSHOT
 */
@Slf4j
public abstract class GatherSlot {

    /**
     * 采集槽名称
     */
    private String name;

    /**
     * 采集槽主机
     */
    private GatherHost host;

    /**
     * 数据包解析器
     */
    protected IDataParser dataParser;

    protected GatherSlot(GatherHost host) {
        this.host = host;
        this.name = String.format("%s-host-%d",
                host.getName(), Instant.now().toEpochMilli());
    }

    /**
     * 设置数据包解析器
     *
     * @param parser 解析器名称
     * @throws Exception
     */
    public void setDataParser(String parser) throws Exception {
        String clazzName = String.format("%s.%s", "com.incarcloud.boar.datapack", parser);
        Class<?> clazz = Class.forName(clazzName);
        this.dataParser = (IDataParser) clazz.newInstance();
    }

    /**
     * 启动服务
     */
    public abstract void startup() throws InterruptedException;

    /**
     * 停止服务
     */
    public abstract void shutdown();

    /**
     * 采集槽实例名称
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 获取传输协议
     *
     * @return tcp/udp/mqtt
     */
    public abstract String getTransportProtocol();

    /**
     * 获取监听端口
     *
     * @return
     */
    public abstract int getListenPort();
}
