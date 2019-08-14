package com.yee.bigdata.common.serialization;

import java.util.Map;

public class DoubleSerializer implements Serializer<Double> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    @Override
    public byte[] serialize(String topic, Double data) {
        if (data == null)
            return null;

        long bits = Double.doubleToLongBits(data);
        return new byte[] {
                (byte) (bits >>> 56),
                (byte) (bits >>> 48),
                (byte) (bits >>> 40),
                (byte) (bits >>> 32),
                (byte) (bits >>> 24),
                (byte) (bits >>> 16),
                (byte) (bits >>> 8),
                (byte) bits
        };
    }

    @Override
    public void close() {
        // nothing to do
    }
}