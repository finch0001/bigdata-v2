package com.yee.bigdata.common.serialization;

import java.io.Closeable;
import java.util.Map;

/**
 *
 * @param <T> Type to be deserialized into.
 *
 * A class that implements this interface is expected to have a constructor with no parameter.
 */
public interface Deserializer<T> extends Closeable {

    /**
     * Configure this class.
     * @param configs configs in key/value pairs
     * @param isKey whether is for key or value
     */
    public void configure(Map<String, ?> configs, boolean isKey);

    /**
     *
     * @param topic topic associated with the data
     * @param data serialized bytes
     * @return deserialized typed data
     */
    public T deserialize(String topic, byte[] data);

    @Override
    public void close();
}