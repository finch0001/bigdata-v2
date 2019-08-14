package com.yee.bigdata.common.serialization;

import com.yee.bigdata.common.util.Bytes;

import java.util.Map;

public class BytesSerializer implements Serializer<Bytes> {

    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    public byte[] serialize(String topic, Bytes data) {
        if (data == null)
            return null;

        return data.get();
    }

    public void close() {
        // nothing to do
    }
}
