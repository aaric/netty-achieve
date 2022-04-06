package com.incarcloud.boar.gather;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.Date;

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
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 1.初始化对象
        // 初始化接收时间
        Date receiveTime = Date.from(Instant.now());
//        IDataParser parser = this.slot.getDataParser();
        RedisTemplate<String, String> redisTemplate = this.slot.getRedisTemplate();
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        // 2.打印数据包信息
        ByteBuf buffer = (ByteBuf) msg;
//        log.info("IDataParser: {}, Receive Bytes: {}", parser.getClass().getSimpleName(), ByteBufUtil.hexDump(buffer));

        // 3.处理数据包
//        try {
//            // 3.1 获得设备号
//            String deviceId = parser.getDeviceSn(buffer);
//            if (StringUtils.isBlank(deviceId)) {
//                log.info("No deviceId!!!");
//                return;
//            }
//
//            // 3.2 获取设备校验Key
//            String deviceKeyBase64String = hashOperations.get(Constants.CacheNamespaceKey.CACHE_DEVICE_KEY_HASH, deviceId);
//            log.debug("deviceId: {}, deviceKeyBase64String: {}", deviceId, deviceKeyBase64String);
//            if (StringUtils.isEmpty(deviceKeyBase64String)) {
//                log.info("No device key!!!");
//                return;
//            }
//            parser.setDeviceKey(deviceId, Base64.getDecoder().decode(deviceKeyBase64String));
//
//            // 3.3 轻量解析（分解，校验，解密）
//            List<DataPack> dataPackList = parser.extract(buffer);
//            if (null == dataPackList || 0 == dataPackList.size()) {
//                log.info("No packs!!!");
//                return;
//            }
//
//            // 3.4 获取vin信息
//            String vin = hashOperations.get(Constants.CacheNamespaceKey.CACHE_DEVICE_ID_HASH, deviceId);
//            if (StringUtils.isEmpty(vin)) {
//                log.info("No vin!!!");
//                return;
//            }
//
//            // 3.5 构建MetaData信息
//            Map<String, Object> metaData = new HashMap<>();
//            metaData.put(Constants.MetaDataMapKey.DEVICE_ID, deviceId);
//            metaData.put(Constants.MetaDataMapKey.VIN, vin);
//
//            // 3.6 上线校验
//            if (Constants.PackType.VALIDATE == dataPackList.get(0).getPackType()) {
//                String bindingInfoString = hashOperations.get(Constants.CacheNamespaceKey.CACHE_DEVICE_MODEL_HASH, deviceId);
//                parser.setVehicleModel(deviceId, GsonFactory.newInstance().createGson().fromJson(bindingInfoString, VehicleModelCode.class));
//            }
//
//            // 3.7 发送给MQ并回复设备
//            sendToKafkaQueue(ctx.channel(), dataPackList, vin, receiveTime);
//
//            // TODO 3.8 缓存vin与Channel的关系
//        } catch (Exception e) {
//            log.error("channelRead", e);
//        }

    }

    /**
     * 发送消息到Kafka队列
     */
//    private void sendToKafkaQueue(Channel channel, List<DataPack> dataPackList, String vin, Date receiveTime)
//            throws UnsupportedEncodingException {
//        // 获取Topic和kafkaTemplate对象
//        String kafkaTopic = this.slot.getKafkaTopic();
//        KafkaTemplate<String, String> kafkaTemplate = this.slot.getKafkaTemplate();
//        IDataParser parser = this.slot.getDataParser();
//
//        // 封装MQMsg对象，并回复设备
//        for (DataPack dp : dataPackList) {
//            // 填充接收时间
//            dp.setReceiveTime(receiveTime);
//
//            // 发送消息给Kafka
//            MQMsg mqMsg = new MQMsg(String.format("%s|%s", dp.getMark(), vin), dp.serializeToBytes());
//            kafkaTemplate.send(kafkaTopic, GsonFactory.newInstance().createGson().toJson(mqMsg));
//
//            // 回复设备信息
//            // TODO 需要考虑Kafka服务挂起的情况
//            ByteBuf responseBytes = parser.createResponse(dp, ERespReason.OK);
//            if (null != responseBytes) {
//                log.info("Response Bytes: {}", responseBytes);
//                channel.writeAndFlush(responseBytes);
//            }
//        }
//
//    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelInactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    // 读超时
                    log.info("read timeout...");
                    // 主动断开与客户端连接
                    ctx.close();
                    break;
                case WRITER_IDLE:
                    // 写超时
                    log.info("write timeout...");
                    break;
                case ALL_IDLE:
                    // 读/写超时
                    log.info("read and write timeout...");
                    break;
                default:
            }
        }
    }
}
