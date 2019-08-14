package com.yee.bigdata.common.serialization;

import com.yee.bigdata.common.errors.AppLogicException;

import java.util.Map;

public class LongDeserializer implements Deserializer<Long> {

    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    public Long deserialize(String topic, byte[] data) {
        if (data == null)
            return null;
        if (data.length != 8) {
            throw new AppLogicException("Size of data received by LongDeserializer is not 8");
        }

        long value = 0;
        for (byte b : data) {
            value <<= 8;
            value |= b & 0xFF;
        }
        return value;
    }

    public void close() {
        // nothing to do
    }
}
