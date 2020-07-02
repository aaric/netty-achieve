package com.sample.netty;

import com.github.io.protocol.annotation.ByteOrder;
import com.github.io.protocol.annotation.Decimal;
import com.github.io.protocol.annotation.Element;
import com.github.io.protocol.annotation.Number;
import com.github.io.protocol.core.ProtocolEngine;
import com.incarcloud.boar.util.DataPackUtil;
import io.netty.buffer.ByteBufUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ProtocolEngineJtt808Tests
 *
 * @author Aaric, created on 2020-07-01T09:35.
 * @version 1.7.0-SNAPSHOT
 */
@Slf4j
public class ProtocolEngineJtt808Tests {

    @Data
    @Accessors(chain = true)
    private static class MsgProp {
        /*@Number(width = 2)
        private int retain = 0;
        @Number(width = 1)
        private int isSubPacket = 0;
        @Number(width = 3)
        private int encryptMode = 0;
        @Number(width = 10, order = ByteOrder.BigEndian)
        private int msgLength;*/
        @Number(width = 16, order = ByteOrder.BigEndian) //set prev 6bit=0
        private int msgLength;
    }

    @Data
    public static class Header {
        @Number(width = 16, order = ByteOrder.BigEndian)
        private int msgId;
        @Element
        private MsgProp msgProp;
        @Number(width = 8, length = "lengthDeviceSn", encoder = "encodeDeviceSn", decoder = "decodeDeviceSn")
        private long deviceSn;
        @Number(width = 16, order = ByteOrder.BigEndian)
        private int msgSn;

        public int lengthDeviceSn() {
            return 6;
        }

        public byte[] encodeDeviceSn() {
            if (0 > deviceSn) {
                return new byte[]{};
            }
            return DataPackUtil.getBCDBytes(String.format("%012d", deviceSn));
        }

        public void decodeDeviceSn(byte[] content) {
            this.deviceSn = 999999999999L; //fake
        }
    }

    @Data
    public static class Jtt808Packet {
        @Number(width = 8)
        private int flagH = 0x7e;
        @Element
        private Header header;
        @Number(width = 8, length = "lengthMsgContent", encoder = "encodeMsgContent", decoder = "decodeMsgContent")
        private byte[] msgContent;
        @Number(width = 8)
        private byte validCode = 0x00;
        @Number(width = 8)
        private int flagT = 0x7e;

        public int lengthMsgContent() {
            return header.getMsgProp().getMsgLength();
        }

        public byte[] encodeMsgContent() {
            if (0 == lengthMsgContent()) {
                return new byte[]{};
            }
            return this.msgContent;
        }

        public void decodeMsgContent(byte[] content) {
            if (0 == lengthMsgContent()) {
                this.msgContent = new byte[]{};
            } else {
                this.msgContent = content;
            }
        }

        public static byte[] addCheck(byte[] bytes) {
            byte check = 0x00;
            for (int i = 1; i <= bytes.length - 3; i++) {
                check ^= bytes[i];
            }
            bytes[bytes.length - 2] = check;
            return bytes;
        }

        public static byte[] toConvert(byte[] bytes) {
            // 标识位(0x7e)
            if (0x7E == (bytes[0] & 0xFF) && 0x7E == (bytes[bytes.length - 1] & 0xFF)) {
                // 转义规则：0x7E->0x7D0x02, 0x7D->0x7D0x01
                List<Byte> byteList = new ArrayList<>();
                byteList.add(bytes[0]); // 0x7E
                for (int i = 1; i < bytes.length - 1; i++) {
                    if (0x7E == (bytes[i] & 0xFF)) {
                        // 0x7E->0x7D0x02
                        byteList.add((byte) 0x7D);
                        byteList.add((byte) 0x02);
                    } else if (0x7D == (bytes[i] & 0xFF)) {
                        // 0x7D->0x7D0x01
                        byteList.add((byte) 0x7D);
                        byteList.add((byte) 0x01);
                    } else {
                        byteList.add(bytes[i]);
                    }
                }
                byteList.add(bytes[bytes.length - 1]); //0x7E

                // List<Byte> --> byte[]
                byte[] shiftBytes = new byte[byteList.size()];
                for (int i = 0; i < shiftBytes.length; i++) {
                    shiftBytes[i] = byteList.get(i).byteValue();
                }

                return shiftBytes;
            }
            return null;
        }
    }

    @Data
    public static class Position {
        // byte-8, word-16, dword-32
        @Number(width = 32, order = ByteOrder.BigEndian)
        private int alarmFlag = 0;
        @Number(width = 32, order = ByteOrder.BigEndian)
        private int carFlag = 0;
        @Decimal(width = 32, order = ByteOrder.BigEndian, scale = 0.000001, precision = 6)
        private double longitude;
        @Decimal(width = 32, order = ByteOrder.BigEndian, scale = 0.000001, precision = 6)
        private double latitude;
        @Number(width = 16, order = ByteOrder.BigEndian)
        private int altitude;
        @Decimal(width = 16, order = ByteOrder.BigEndian, scale = 0.1, precision = 1)
        private float speed;
        @Number(width = 16, order = ByteOrder.BigEndian)
        private int direction;
        @Number(width = 8, length = "lengthDetectionTime", encoder = "encodeDetectionTime", decoder = "decodeDetectionTime")
        private long detectionTime;

        public int lengthDetectionTime() {
            return 6;
        }

        public byte[] encodeDetectionTime() {
            if (0 > detectionTime) {
                return new byte[]{};
            }
            return DataPackUtil.getBCDBytes(String.format("%012d", detectionTime));
        }

        public void decodeDetectionTime(byte[] content) {
            this.detectionTime = 200702120000L; //fake
        }
    }

    @Data
    public static class PositionExtra {
        // fake
    }

    @Test
    public void testEncode() throws Exception {
        ProtocolEngine engine = new ProtocolEngine();

        Position position = new Position();
        position.setLatitude(114.403663D);
        position.setLongitude(30.475756D);
        position.setAltitude(15);
        position.setSpeed(0);
        position.setDirection(0);
        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        position.setDetectionTime(Long.valueOf(dateFormat.format(Date.from(Instant.now()))));

        byte[] content = engine.encode(position);

        Header header = new Header();
        header.setMsgId(0x0200);
        header.setMsgProp(new MsgProp().setMsgLength(content.length));
        header.setDeviceSn(18168000002L);
        header.setMsgSn(0x0024);

        Jtt808Packet packet = new Jtt808Packet();
        packet.setHeader(header);
        packet.setMsgContent(content);

        byte[] genBytes = Jtt808Packet.toConvert(Jtt808Packet.addCheck(engine.encode(packet)));
        log.debug("{}", ByteBufUtil.hexDump(genBytes));
    }
}
