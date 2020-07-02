package com.sample.netty;

import com.github.io.protocol.annotation.AsciiString;
import com.github.io.protocol.annotation.ByteOrder;
import com.github.io.protocol.annotation.Number;
import com.github.io.protocol.core.ProtocolEngine;
import io.netty.buffer.ByteBufUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * ProtocolEngineTests
 *
 * @author Aaric, created on 2020-06-30T09:47.
 * @version 1.7.0-SNAPSHOT
 */
@Slf4j
public class ProtocolEngineTests {

    @Data
    public static class ProtocolTest {
        @Number(width = 16, order = ByteOrder.BigEndian)
        private int header;

        @Number(width = 8)
        private int version;

        @Number(width = 16, order = ByteOrder.BigEndian)
        private int contentLength;

        @AsciiString(length = "getContentLength")
        private String content;
    }

    @Test
    public void testEncode() throws Exception {
        ProtocolEngine engine = new ProtocolEngine();

        ProtocolTest test = new ProtocolTest();
        test.setHeader(0x2882);
        test.setVersion(1);
        test.setContent("Hello World!");
        test.setContentLength(test.getContent().length());

        byte[] helloBytes = engine.encode(test);
        // 288201000c48656c6c6f20576f726c6421
        log.debug("{}", ByteBufUtil.hexDump(helloBytes));
    }

    @Test
    public void testDecode() throws Exception {
        ProtocolEngine engine = new ProtocolEngine();
        byte[] helloBytes = ByteBufUtil.decodeHexDump("288201000c48656c6c6f20576f726c6421");
        ProtocolTest test = engine.decode(helloBytes, ProtocolTest.class);
        log.debug("{}", test);
    }
}
