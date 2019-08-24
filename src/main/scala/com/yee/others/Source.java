package com.yee.others;

import java.util.Map;


public interface Source<T> extends AutoCloseable {
    /**
     * Open connector with configuration
     *
     * @param config initialization config
     * @param sourceContext
     * @throws Exception IO type exceptions when opening a connector
     */
    void open(final Map<String, Object> config, SourceContext sourceContext) throws Exception;

    /**
     * Reads the next message from source.
     * If source does not have any new messages, this call should block.
     * @return next message from source.  The return result should never be null
     * @throws Exception
     */
    Record<T> read() throws Exception;
}
