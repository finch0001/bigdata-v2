package com.yee.lucene;

import java.io.IOException;

/**
 * This exception is thrown when the <code>write.lock</code>
 * could not be acquired.  This
 * happens when a writer tries to open an index
 * that another writer already has open.
 * @see LockFactory#obtainLock(Directory, String)
 */
public class LockObtainFailedException extends IOException {
    public LockObtainFailedException(String message) {
        super(message);
    }

    public LockObtainFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
