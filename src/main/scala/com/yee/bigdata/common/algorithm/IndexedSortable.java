package com.yee.bigdata.common.algorithm;

public interface IndexedSortable {

    /**
     * Compare items at the given addresses consistent with the semantics of
     * {@link java.util.Comparator#compare(Object, Object)}.
     */
    int compare(int i, int j);

    /**
     * Swap items at the given addresses.
     */
    void swap(int i, int j);
}
