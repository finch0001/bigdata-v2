package com.yee.bigdata.common.io;


/**
 * @lucene.experimental
 */
public interface Store {

    byte[] takeBuffer(int bufferSize);

    void putBuffer(byte[] buffer);

}

