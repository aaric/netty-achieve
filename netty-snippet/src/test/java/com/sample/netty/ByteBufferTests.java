package com.sample.netty;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ByteBufferTests
 *
 * @author Aaric, created on 2022-03-09T10:45.
 * @version 2.0.0-SNAPSHOT
 */
@Slf4j
public class ByteBufferTests {

    @Test
    public void testFileChannel() {
        String file = ClassLoader.getSystemResource("data.txt").getFile();
        try (FileChannel channel = new FileInputStream(file).getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                int length = channel.read(buffer);
                if (-1 == length) {
                    break;
                }

                // 切换到读模式
                buffer.flip();

                // 打印字符
                while (buffer.hasRemaining()) {
                    byte letter = buffer.get();
                    log.info("{}", letter);
                }

                // 切换到写模式
                buffer.clear();
            }
        } catch (Exception e) {
            log.error("testFileChannel exception", e);
        }
    }
}
