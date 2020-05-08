package com.incarcloud.boar.gather;

import com.incarcloud.boar.datapack.IDataParser;
import com.incarcloud.boar.share.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

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
        // 1.初始化对象
        IDataParser parser = slot.getDataParser();
        RedisTemplate<String, String> redisTemplate = slot.getRedisTemplate();
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        // 2.打印数据包信息
        ByteBuf buffer = (ByteBuf) msg;
        log.info("IDataParser: {}, Receive Bytes: {}", parser.getClass().getSimpleName(), ByteBufUtil.hexDump(buffer));

        // 3.获得设备号
        String deviceId = parser.getDeviceSn(buffer);
        if (StringUtils.isBlank(deviceId)) {
            return;
        }

        // 4.获取设备校验Key
        String deviceKeyBase64String = hashOperations.get(Constants.CacheNamespaceKey.CACHE_DEVICE_KEY_HASH, deviceId);
        if (StringUtils.isEmpty(deviceKeyBase64String)) {
            return;
        }
        log.debug("deviceId: {}, deviceKeyBase64String: {}", deviceId, deviceKeyBase64String);
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
