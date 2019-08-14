package com.yee.bigdata.common.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * A utility class to manage a set of locks. Each lock is identified by a String which serves
 * as a key. Typical usage is: <pre>
 * class Example {
 *   private final static KeyLocker&lt;String&gt; locker = new Locker&lt;String&gt;();
 *   public void foo(String s){
 *     Lock lock = locker.acquireLock(s);
 *     try {
 *       // whatever
 *     }finally{
 *       lock.unlock();
 *     }
 *   }
 * }
 * </pre>
 */
public class KeyLocker<K> {
    // The number of lock we want to easily support. It's not a maximum.
    private static final int NB_CONCURRENT_LOCKS = 1000;

    private final WeakObjectPool<K, ReentrantLock> lockPool =
            new WeakObjectPool<K, ReentrantLock>(
                    new WeakObjectPool.ObjectFactory<K, ReentrantLock>() {
                        @Override
                        public ReentrantLock createObject(K key) {
                            return new ReentrantLock();
                        }
                    },
                    NB_CONCURRENT_LOCKS);

    /**
     * Return a lock for the given key. The lock is already locked.
     *
     * @param key
     */
    public ReentrantLock acquireLock(K key) {
        if (key == null) throw new IllegalArgumentException("key must not be null");

        lockPool.purge();
        ReentrantLock lock = lockPool.get(key);

        lock.lock();
        return lock;
    }

    /**
     * Acquire locks for a set of keys. The keys will be
     * sorted internally to avoid possible deadlock.
     *
     * @throws ClassCastException if the given {@code keys}
     *    contains elements that are not mutually comparable
     */
    public Map<K, Lock> acquireLocks(Set<? extends K> keys) {
        Object[] keyArray = keys.toArray();
        Arrays.sort(keyArray);

        lockPool.purge();
        Map<K, Lock> locks = new LinkedHashMap<K, Lock>(keyArray.length);
        for (Object o : keyArray) {
            @SuppressWarnings("unchecked")
            K key = (K)o;
            ReentrantLock lock = lockPool.get(key);
            locks.put(key, lock);
        }

        for (Lock lock : locks.values()) {
            lock.lock();
        }
        return locks;
    }
}