package com.yee.bigdata.common.serialization;

import java.util.Map;

public class ByteArraySerializer implements Serializer<byte[]> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    @Override
    public byte[] serialize(String topic, byte[] data) {
        return data;
    }

    @Override
    public void close() {
        // nothing to do
    }
}