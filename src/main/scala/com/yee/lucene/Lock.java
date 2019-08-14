package com.yee.lucene;

import java.io.Closeable;
import java.io.IOException;

/** An interprocess mutex lock.
 * <p>Typical use might look like:<pre class="prettyprint">
 *   try (final Lock lock = directory.obtainLock("my.lock")) {
 *     // ... code to execute while locked ...
 *   }
 * </pre>
 *
 * @see Directory#obtainLock(String)
 *
 * @lucene.internal
 */
public abstract class Lock implements Closeable {

    /**
     * Releases exclusive access.
     * <p>
     * Note that exceptions thrown from close may require
     * human intervention, as it may mean the lock was no
     * longer valid, or that fs permissions prevent removal
     * of the lock file, or other reasons.
     * <p>
     * {@inheritDoc}
     * @throws LockReleaseFailedException optional specific exception) if
     *         the lock could not be properly released.
     */
    public abstract void close() throws IOException;

    /**
     * Best effort check that this lock is still valid. Locks
     * could become invalidated externally for a number of reasons,
     * for example if a user deletes the lock file manually or
     * when a network filesystem is in use.
     * @throws IOException if the lock is no longer valid.
     */
    public abstract void ensureValid() throws IOException;
}
