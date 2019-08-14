package com.yee.bigdata.common.algorithm;

/**
 * Dictionary interface
 *
 * Dictionary indexes should be either bytes or shorts, only positive. (The
 * first bit is reserved for detecting whether something is compressed or not).
 */
public interface Dictionary {
    byte NOT_IN_DICTIONARY = -1;

    void init(int initialSize);
    /**
     * Gets an entry from the dictionary.
     *
     * @param idx index of the entry
     * @return the entry, or null if non existent
     */
    byte[] getEntry(short idx);

    /**
     * Finds the index of an entry.
     * If no entry found, we add it.
     *
     * @param data the byte array that we're looking up
     * @param offset Offset into <code>data</code> to add to Dictionary.
     * @param length Length beyond <code>offset</code> that comprises entry; must be &gt; 0.
     * @return the index of the entry, or {@link #NOT_IN_DICTIONARY} if not found
     */
    short findEntry(byte[] data, int offset, int length);

    /**
     * Adds an entry to the dictionary.
     * Be careful using this method.  It will add an entry to the
     * dictionary even if it already has an entry for the same data.
     * Call {{@link #findEntry(byte[], int, int)}} to add without duplicating
     * dictionary entries.
     *
     * @param data the entry to add
     * @param offset Offset into <code>data</code> to add to Dictionary.
     * @param length Length beyond <code>offset</code> that comprises entry; must be &gt; 0.
     * @return the index of the entry
     */

    short addEntry(byte[] data, int offset, int length);

    /**
     * Flushes the dictionary, empties all values.
     */
    void clear();
}
