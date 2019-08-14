package com.yee.lucene;

import java.io.IOException;

/**
 * Random Access Index API.
 * Unlike {@link IndexInput}, this has no concept of file position, all reads
 * are absolute. However, like IndexInput, it is only intended for use by a single thread.
 */
public interface RandomAccessInput {

    /**
     * Reads a byte at the given position in the file
     * @see DataInput#readByte
     */
    public byte readByte(long pos) throws IOException;
    /**
     * Reads a short at the given position in the file
     * @see DataInput#readShort
     */
    public short readShort(long pos) throws IOException;
    /**
     * Reads an integer at the given position in the file
     * @see DataInput#readInt
     */
    public int readInt(long pos) throws IOException;
    /**
     * Reads a long at the given position in the file
     * @see DataInput#readLong
     */
    public long readLong(long pos) throws IOException;
}

