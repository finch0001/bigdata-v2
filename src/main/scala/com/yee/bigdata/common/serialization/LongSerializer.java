package com.yee.bigdata.common.serialization;

import java.util.Map;

public class LongSerializer implements Serializer<Long> {

    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    public byte[] serialize(String topic, Long data) {
        if (data == null)
            return null;

        return new byte[] {
                (byte) (data >>> 56),
                (byte) (data >>> 48),
                (byte) (data >>> 40),
                (byte) (data >>> 32),
                (byte) (data >>> 24),
                (byte) (data >>> 16),
                (byte) (data >>> 8),
                data.byteValue()
        };
    }

    public void close() {
        // nothing to do
    }
}
