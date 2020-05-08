package com.incarcloud.boar.gather;

import com.incarcloud.boar.datapack.IDataParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 采集槽处理器
 *
 * @author Aaric, created on 2020-05-07T19:07.
 * @version 1.3.0-SNAPSHOT
 */
@Slf4j
public class GatherChannelHandler extends ChannelInboundHandlerAdapter {

    /**
     * 所属的采集槽
     */
    private GatherSlot slot;

    public GatherChannelHandler(GatherSlot slot) {
        this.slot = slot;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 初始化对象
        IDataParser parser = slot.getDataParser();
        log.info(slot.getKafkaTopic());

        // 打印数据包信息
        ByteBuf buffer = (ByteBuf) msg;
        log.info("IDataParser: {}, Receive Bytes: {}", parser.getClass().getSimpleName(), ByteBufUtil.hexDump(buffer));

        // 1.获得设备号
        String deviceId = parser.getDeviceSn(buffer);
        log.debug("deviceId: " + deviceId);
        if (StringUtils.isBlank(deviceId)) {
            return;
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught", cause);
        ctx.close();
    }
}
