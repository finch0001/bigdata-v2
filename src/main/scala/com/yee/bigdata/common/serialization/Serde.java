package com.yee.bigdata.common.serialization;

import java.io.Closeable;
import java.util.Map;

/**
 * The interface for wrapping a serializer and deserializer for the given data type.
 *
 * @param <T> Type to be serialized from and deserialized into.
 *
 * A class that implements this interface is expected to have a constructor with no parameter.
 */
public interface Serde<T> extends Closeable {

    /**
     * Configure this class, which will configure the underlying serializer and deserializer.
     *
     * @param configs configs in key/value pairs
     * @param isKey whether is for key or value
     */
    void configure(Map<String, ?> configs, boolean isKey);

    /**
     * Close this serde class, which will close the underlying serializer and deserializer.
     * This method has to be idempotent because it might be called multiple times.
     */
    @Override
    void close();

    Serializer<T> serializer();

    Deserializer<T> deserializer();
}