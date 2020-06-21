package com.sample.netty;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

/**
 * NioTests
 *
 * @author Aaric, created on 2020-06-21T21:32.
 * @version 1.5.0-SNAPSHOT
 */
@Slf4j
public class NioTests {

    @Test
    public void testIntBuffer() {
        IntBuffer buffer = IntBuffer.allocate(10);

        for (int i = 0; i < 10; i++) {
            buffer.put(i);
        }

        buffer.flip();

        while (buffer.hasRemaining()) {
            log.debug("-> {}", buffer.get());
        }
    }
}
