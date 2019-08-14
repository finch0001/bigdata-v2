package com.yee.bigdata.common.serialization;

import com.yee.bigdata.common.util.Bytes;

import java.util.Map;

public class BytesDeserializer implements Deserializer<Bytes> {

    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    public Bytes deserialize(String topic, byte[] data) {
        if (data == null)
            return null;

        return new Bytes(data);
    }

    public void close() {
        // nothing to do
    }
}
