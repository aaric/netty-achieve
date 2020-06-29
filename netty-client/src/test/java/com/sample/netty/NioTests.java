package com.sample.netty;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
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
    @Disabled
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
    @Disabled
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

    @Test
    public void testByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(512);

        buffer.putShort((short) 127);
        buffer.putInt(10);
        buffer.putLong(10000);
        buffer.putFloat(180.0F);
        buffer.putDouble(3.14D);
        buffer.putChar('a');

        buffer.flip();

        log.debug("{}", buffer.getShort());
        log.debug("{}", buffer.getInt());
        log.debug("{}", buffer.getLong());
        log.debug("{}", buffer.getFloat());
        log.debug("{}", buffer.getDouble());
        log.debug("{}", buffer.getChar());
    }

    @Test
    public void testBufferSlice() {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put((byte) i);
        }

        buffer.position(2);
        buffer.limit(6);

        ByteBuffer sliceBuffer = buffer.slice();

        for (int i = 0; i < sliceBuffer.capacity(); i++) {
            buffer.put(i, (byte) (buffer.get(i) * 2));
        }

        buffer.position(0);
        buffer.limit(buffer.capacity());

        while (buffer.hasRemaining()) {
            log.debug("{}", buffer.get());
        }
    }
}
