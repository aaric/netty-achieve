package com.incarcloud.boar.gather;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

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
        ByteBuf buffer = (ByteBuf) msg;

        log.info("msg: {}", ByteBufUtil.hexDump(buffer));
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
