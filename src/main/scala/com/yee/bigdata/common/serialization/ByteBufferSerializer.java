package com.yee.bigdata.common.serialization;

import java.nio.ByteBuffer;
import java.util.Map;

public class ByteBufferSerializer implements Serializer<ByteBuffer> {

    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    public byte[] serialize(String topic, ByteBuffer data) {
        if (data == null)
            return null;

        data.rewind();

        if (data.hasArray()) {
            byte[] arr = data.array();
            if (data.arrayOffset() == 0 && arr.length == data.remaining()) {
                return arr;
            }
        }

        byte[] ret = new byte[data.remaining()];
        data.get(ret, 0, ret.length);
        data.rewind();
        return ret;
    }

    public void close() {
        // nothing to do
    }
}
