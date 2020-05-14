package com.sample.netty;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * VinTests
 *
 * @author Aaric, created on 2020-05-11T09:53.
 * @version 1.4.0-SNAPSHOT
 */
@Slf4j
public class VinTests {

    @Test
    public void testStringHashCode() {
        String vin = MessageFormat.format("LFV2A21J880{0,number,000000}", 2020); //LFV2A21J880002020
        Assertions.assertEquals("LFV2A21J880002020", vin);
        Assertions.assertEquals(2108083316, Math.abs(vin.hashCode()));
    }

    @Test
    public void testVinHashCodeAvg() {
        int mode = 3;
        ConcurrentMap<Integer, Set<String>> vinSetMap = new ConcurrentHashMap<>();

        for (int i = 0; i < 100000; i++) {
            String vin = MessageFormat.format("LFV2A21J880{0,number,000000}", i);
            //vin = StringUtils.reverse(vin); //Reverse
            int vinHashCodeAbs = Math.abs(vin.hashCode());

            int modeKey = vinHashCodeAbs % mode;
            Set<String> vinSet = vinSetMap.get(modeKey);
            if (null == vinSet) {
                vinSet = new HashSet<>();
                vinSetMap.put(modeKey, vinSet);
            }

            vinSet.add(vin);
        }

        for (int j = 0; j < mode; j++) {
            log.info("mode {} total: {}", j, vinSetMap.get(j).size());
        }

        Assertions.assertEquals(3, vinSetMap.size());
    }
}
