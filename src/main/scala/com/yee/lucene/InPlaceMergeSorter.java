package com.yee.lucene;



/** {@link Sorter} implementation based on the merge-sort algorithm that merges
 *  in place (no extra memory will be allocated). Small arrays are sorted with
 *  insertion sort.
 *  @lucene.internal */
public abstract class InPlaceMergeSorter extends Sorter {

    /** Create a new {@link InPlaceMergeSorter} */
    public InPlaceMergeSorter() {}

    @Override
    public final void sort(int from, int to) {
        checkRange(from, to);
        mergeSort(from, to);
    }

    void mergeSort(int from, int to) {
        if (to - from < BINARY_SORT_THRESHOLD) {
            binarySort(from, to);
        } else {
            final int mid = (from + to) >>> 1;
            mergeSort(from, mid);
            mergeSort(mid, to);
            mergeInPlace(from, mid, to);
        }
    }

}