package com.yee.solr;


import java.util.Map;
import java.util.function.Function;

// A cache backed by a map
public class MapBackedCache<K, V> implements Cache<K, V> {

    protected final Map<K, V> map;

    public MapBackedCache(Map<K, V> map) {
        this.map = map;
    }

    @Override
    public V put(K key, V val) {
        return map.put(key, val);
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public V remove(K key) {
        return map.remove(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }
}