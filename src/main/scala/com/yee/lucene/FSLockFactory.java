package com.yee.lucene;


import java.io.IOException;

/**
 * Base class for file system based locking implementation.
 * This class is explicitly checking that the passed {@link Directory}
 * is an {@link FSDirectory}.
 */
public abstract class FSLockFactory extends LockFactory {

    /** Returns the default locking implementation for this platform.
     * This method currently returns always {@link NativeFSLockFactory}.
     */
    public static final FSLockFactory getDefault() {
        return NativeFSLockFactory.INSTANCE;
    }

    @Override
    public final Lock obtainLock(Directory dir, String lockName) throws IOException {
        if (!(dir instanceof FSDirectory)) {
            throw new UnsupportedOperationException(getClass().getSimpleName() + " can only be used with FSDirectory subclasses, got: " + dir);
        }
        return obtainFSLock((FSDirectory) dir, lockName);
    }

    /**
     * Implement this method to obtain a lock for a FSDirectory instance.
     * @throws IOException if the lock could not be obtained.
     */
    protected abstract Lock obtainFSLock(FSDirectory dir, String lockName) throws IOException;

}

