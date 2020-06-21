package com.sample.netty;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

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

    @Test
    public void testChannelWrite() throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream("hello.log");
        FileChannel fileChannel = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byte[] data = "welcome to earth.".getBytes();
        for (int i = 0; i < data.length; i++) {
            byteBuffer.put(data[i]);
        }
        byteBuffer.flip();

        fileChannel.write(byteBuffer);

        fileOutputStream.close();
    }

    @Test
    public void testChannelRead() throws Exception {
        FileInputStream fileInputStream = new FileInputStream("hello.log");
        FileChannel fileChannel = fileInputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        fileChannel.read(byteBuffer);
        byteBuffer.flip();

        while (byteBuffer.remaining() > 0) {
            log.debug("-> {}", (char) byteBuffer.get());
        }

        fileInputStream.close();
    }
}
