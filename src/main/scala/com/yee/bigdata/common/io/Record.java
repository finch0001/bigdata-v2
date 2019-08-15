package com.yee.bigdata.common.io;


import java.io.IOException;

/**
 * Interface that is implemented by generated classes.
 *
 */
public interface Record {
    public void serialize(OutputArchive archive, String tag)
            throws IOException;
    public void deserialize(InputArchive archive, String tag)
            throws IOException;
}

