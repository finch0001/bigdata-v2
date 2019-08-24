package com.yee.others;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Pulsar Connect's Record interface. Record encapsulates the information about a record being read from a Source.
 */
public interface Record<T> {

    /**
     * If the record originated from a topic, report the topic name.
     */
    default Optional<String> getTopicName() {
        return Optional.empty();
    }

    /**
     * Return a key if the key has one associated.
     */
    default Optional<String> getKey() {
        return Optional.empty();
    }

    /**
     * Retrieves the actual data of the record.
     *
     * @return The record data
     */
    T getValue();

    /**
     * Retrieves the event time of the record from the source.
     *
     * @return millis since epoch
     */
    default Optional<Long> getEventTime() {
        return Optional.empty();
    }

    /**
     * Retrieves the partition information if any of the record.
     *
     * @return The partition id where the
     */
    default Optional<String> getPartitionId() {
        return Optional.empty();
    }

    /**
     * Retrieves the sequence of the record from a source partition.
     *
     * @return Sequence Id associated with the record
     */
    default Optional<Long> getRecordSequence() {
        return Optional.empty();
    }

    /**
     * Retrieves user-defined properties attached to record.
     *
     * @return Map of user-properties
     */
    default Map<String, String> getProperties() {
        return Collections.emptyMap();
    }

    /**
     * Acknowledge that this record is fully processed.
     */
    default void ack() {
    }

    /**
     * To indicate that this record has failed to be processed.
     */
    default void fail() {
    }

    /**
     * To support message routing on a per message basis.
     *
     * @return The topic this message should be written to
     */
    default Optional<String> getDestinationTopic() {
        return Optional.empty();
    }

    default Optional<Message<T>> getMessage() {
        return Optional.empty();
    }
}
