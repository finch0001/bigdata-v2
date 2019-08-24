package com.yee.solr;


import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


/**
 * Simple object cache with a type-safe accessor.
 */
public class ObjectCache extends MapBackedCache<String, Object> implements SolrCloseable {

    private volatile boolean isClosed;

    public ObjectCache() {
        super(new ConcurrentHashMap<>());
    }

    private void ensureNotClosed() {
        if (isClosed) {
            throw new RuntimeException("This ObjectCache is already closed.");
        }
    }

    @Override
    public Object put(String key, Object val) {
        ensureNotClosed();
        return super.put(key, val);
    }

    @Override
    public Object get(String key) {
        ensureNotClosed();
        return super.get(key);
    }

    @Override
    public Object remove(String key) {
        ensureNotClosed();
        return super.remove(key);
    }

    @Override
    public void clear() {
        ensureNotClosed();
        super.clear();
    }

    public <T> T get(String key, Class<T> clazz) {
        Object o = get(key);
        if (o == null) {
            return null;
        } else {
            return clazz.cast(o);
        }
    }

    public <T> T computeIfAbsent(String key, Class<T> clazz, Function<String, ? extends T> mappingFunction) {
        ensureNotClosed();
        Object o = super.computeIfAbsent(key, mappingFunction);
        return clazz.cast(o);
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void close() throws IOException {
        isClosed = true;
        map.clear();
    }
}
