package com.yee.lucene;

/**
 * Immutable twin of FixedBitSet.
 */
final class FixedBits implements Bits {

    final long[] bits;
    final int length;

    FixedBits(long[] bits, int length) {
        this.bits = bits;
        this.length = length;
    }

    @Override
    public boolean get(int index) {
        assert index >= 0 && index < length: "index=" + index + ", numBits=" + length;
        int i = index >> 6;               // div 64
        // signed shift will keep a negative index and force an
        // array-index-out-of-bounds-exception, removing the need for an explicit check.
        long bitmask = 1L << index;
        return (bits[i] & bitmask) != 0;
    }

    @Override
    public int length() {
        return length;
    }

}
