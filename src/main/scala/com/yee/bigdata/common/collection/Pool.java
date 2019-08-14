package com.yee.bigdata.common.collection;


/** Simple object pool to prevent GC on small objects passed between threads. */
public interface Pool<T> {
    /** Object helper for objects stored in the pool. */
    public interface PoolObjectHelper<T> {
        /** Called to create an object when one cannot be provided.
         * @return a newly allocated object
         */
        T create();
        /** Called before the object is put in the pool (regardless of whether put succeeds).
         * @param t the object to reset
         */
        void resetBeforeOffer(T t);
    }

    T take();
    void offer(T t);
    int size();
}