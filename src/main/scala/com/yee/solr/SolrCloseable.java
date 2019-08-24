package com.yee.solr;


import java.io.Closeable;

/**
 * A {@link Closeable} that also allows checking whether it's been closed.
 */
public interface SolrCloseable extends Closeable {

    default boolean isClosed() {
        return false;
    }
}
