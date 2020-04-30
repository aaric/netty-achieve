package com.sample.netty;

import io.netty.buffer.ByteBufUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * com.sample.netty
 *
 * @author Aaric, created on 2020-04-30T15:42.
 * @version 1.2.0-SNAPSHOT
 */
@Slf4j
public class NettyTests {

    @Test
    public void testBytesToAscii() {
        byte[] bytes = ByteBufUtil.decodeHexDump("43533230323030313234");
        String content = new String(bytes);
        log.info("content: {}", content);
        Assertions.assertEquals("CS20200124", content);
    }
}
