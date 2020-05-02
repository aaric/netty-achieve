package com.sample.netty.client;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * MySocketServerTests
 *
 * @author Aaric, created on 2020-05-02T14:36.
 * @version 1.2.0-SNAPSHOT
 */
public class MySocketServerTests {

    @Test
    @Disabled
    public void testClient() throws InterruptedException {
        MySocketClient client = new MySocketClient("localhost", 8888);
        client.connect();
    }
}
