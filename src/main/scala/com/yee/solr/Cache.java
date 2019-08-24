package com.yee.solr;


import java.util.Objects;
import java.util.function.Function;

public interface Cache<K, V> {
    V put(K key, V val);

    V get(K key);

    V remove(K key);

    void clear();

    default V computeIfAbsent(K key,
                              Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v;
        if ((v = get(key)) == null) {
            V newValue;
            if ((newValue = mappingFunction.apply(key)) != null) {
                put(key, newValue);
                return newValue;
            }
        }

        return v;
    }

}
