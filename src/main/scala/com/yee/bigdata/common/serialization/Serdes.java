package com.yee.bigdata.common.serialization;

import com.yee.bigdata.common.util.Bytes;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Factory for creating serializers / deserializers.
 */
public class Serdes {

    static private class WrapperSerde<T> implements Serde<T> {
        final private Serializer<T> serializer;
        final private Deserializer<T> deserializer;

        public WrapperSerde(Serializer<T> serializer, Deserializer<T> deserializer) {
            this.serializer = serializer;
            this.deserializer = deserializer;
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            serializer.configure(configs, isKey);
            deserializer.configure(configs, isKey);
        }

        @Override
        public void close() {
            serializer.close();
            deserializer.close();
        }

        @Override
        public Serializer<T> serializer() {
            return serializer;
        }

        @Override
        public Deserializer<T> deserializer() {
            return deserializer;
        }
    }

    static public final class LongSerde extends WrapperSerde<Long> {
        public LongSerde() {
            super(new LongSerializer(), new LongDeserializer());
        }
    }

    static public final class IntegerSerde extends WrapperSerde<Integer> {
        public IntegerSerde() {
            super(new IntegerSerializer(), new IntegerDeserializer());
        }
    }

    static public final class DoubleSerde extends WrapperSerde<Double> {
        public DoubleSerde() {
            super(new DoubleSerializer(), new DoubleDeserializer());
        }
    }

    static public final class StringSerde extends WrapperSerde<String> {
        public StringSerde() {
            super(new StringSerializer(), new StringDeserializer());
        }
    }

    static public final class ByteBufferSerde extends WrapperSerde<ByteBuffer> {
        public ByteBufferSerde() {
            super(new ByteBufferSerializer(), new ByteBufferDeserializer());
        }
    }

    static public final class BytesSerde extends WrapperSerde<Bytes> {
        public BytesSerde() {
            super(new BytesSerializer(), new BytesDeserializer());
        }
    }

    static public final class ByteArraySerde extends WrapperSerde<byte[]> {
        public ByteArraySerde() {
            super(new ByteArraySerializer(), new ByteArrayDeserializer());
        }
    }

    @SuppressWarnings("unchecked")
    static public <T> Serde<T> serdeFrom(Class<T> type) {
        if (String.class.isAssignableFrom(type)) {
            return (Serde<T>) String();
        }

        if (Integer.class.isAssignableFrom(type)) {
            return (Serde<T>) Integer();
        }

        if (Long.class.isAssignableFrom(type)) {
            return (Serde<T>) Long();
        }

        if (Double.class.isAssignableFrom(type)) {
            return (Serde<T>) Double();
        }

        if (byte[].class.isAssignableFrom(type)) {
            return (Serde<T>) ByteArray();
        }

        if (ByteBuffer.class.isAssignableFrom(type)) {
            return (Serde<T>) ByteBuffer();
        }

        if (Bytes.class.isAssignableFrom(type)) {
            return (Serde<T>) Bytes();
        }

        // TODO: we can also serializes objects of type T using generic Java serialization by default
        throw new IllegalArgumentException("Unknown class for built-in serializer");
    }

    /**
     * Construct a serde object from separate serializer and deserializer
     *
     * @param serializer    must not be null.
     * @param deserializer  must not be null.
     */
    static public <T> Serde<T> serdeFrom(final Serializer<T> serializer, final Deserializer<T> deserializer) {
        if (serializer == null) {
            throw new IllegalArgumentException("serializer must not be null");
        }
        if (deserializer == null) {
            throw new IllegalArgumentException("deserializer must not be null");
        }

        return new WrapperSerde<>(serializer, deserializer);
    }

    /*
     * A serde for nullable {@code Long} type.
     */
    static public Serde<Long> Long() {
        return new LongSerde();
    }

    /*
     * A serde for nullable {@code Integer} type.
     */
    static public Serde<Integer> Integer() {
        return new IntegerSerde();
    }

    /*
     * A serde for nullable {@code Double} type.
     */
    static public Serde<Double> Double() {
        return new DoubleSerde();
    }

    /*
     * A serde for nullable {@code String} type.
     */
    static public Serde<String> String() {
        return new StringSerde();
    }

    /*
     * A serde for nullable {@code ByteBuffer} type.
     */
    static public Serde<ByteBuffer> ByteBuffer() {
        return new ByteBufferSerde();
    }

    /*
     * A serde for nullable {@code Bytes} type.
     */
    static public Serde<Bytes> Bytes() {
        return new BytesSerde();
    }

    /*
     * A serde for nullable {@code byte[]} type.
     */
    static public Serde<byte[]> ByteArray() {
        return new ByteArraySerde();
    }
}