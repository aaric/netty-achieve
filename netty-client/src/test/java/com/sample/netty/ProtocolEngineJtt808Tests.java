package com.sample.netty;

import com.github.io.protocol.annotation.Number;
import com.github.io.protocol.annotation.*;
import com.github.io.protocol.core.ProtocolEngine;
import io.netty.buffer.ByteBufUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

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
        @Number(width = 16, order = ByteOrder.BigEndian)
        private int msgLength;
    }

    @Data
    public static class Header {
        @Number(width = 16, order = ByteOrder.BigEndian)
        private int msgId;
        @Element
        private MsgProp msgProp;
        @AsciiString(length = "6")
        private String deviceSn;
        @Number(width = 16, order = ByteOrder.BigEndian)
        private int msgSn;
    }

    @Data
    public static class Jtt808Packet {
        @Number(width = 8)
        private int flagH = 0x7e;
        @Element
        private Header header;
        @Number(width = 8, length = "getMsgContentLength", encoder = "encodeMsgContent", decoder = "decodeMsgContent", sign = Sign.Signed)
        private byte[] msgContent;
        @Number
        private byte validCode;
        @Number(width = 8)
        private int flagT = 0x7e;

        public int getMsgContentLength() {
            return header.getMsgProp().getMsgLength();
        }

        public byte[] encodeMsgContent() {
            if (0 == getMsgContentLength()) {
                return new byte[]{};
            }
            return this.msgContent;
        }

        public void decodeMsgContent(byte[] content) {
            if (0 == getMsgContentLength()) {
                this.msgContent = new byte[]{};
            } else {
                this.msgContent = content;
            }
        }
    }

    @Test
    public void testEncode() throws Exception {
        ProtocolEngine engine = new ProtocolEngine();

        byte[] content = ByteBufUtil.decodeHexDump("0000008000000082015898ca06ca11b000000012000018041711421801040000000630010f31010b");

        Header header = new Header();
        header.setMsgId(0x0200);
        header.setMsgProp(new MsgProp().setMsgLength(content.length));
        header.setDeviceSn("123456");
        header.setMsgSn(0x0024);

        Jtt808Packet packet = new Jtt808Packet();
        packet.setHeader(header);
        packet.setMsgContent(content);
        packet.setValidCode((byte) 0xd4);

        byte[] genBytes = engine.encode(packet);
        log.debug("{}", ByteBufUtil.hexDump(genBytes));
    }
}
