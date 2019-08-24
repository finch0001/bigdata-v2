package com.yee.lucene;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/** An {@code ExecutorService} that executes tasks immediately in the calling thread during submit.
 *
 *  @lucene.internal */
public final class SameThreadExecutorService extends AbstractExecutorService {
    private volatile boolean shutdown;

    @Override
    public void execute(Runnable command) {
        checkShutdown();
        command.run();
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown();
        return Collections.emptyList();
    }

    @Override
    public void shutdown() {
        this.shutdown = true;
    }

    @Override
    public boolean isTerminated() {
        // Simplified: we don't check for any threads hanging in execute (we could
        // introduce an atomic counter, but there seems to be no point).
        return shutdown == true;
    }

    @Override
    public boolean isShutdown() {
        return shutdown == true;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        // See comment in isTerminated();
        return true;
    }

    private void checkShutdown() {
        if (shutdown) {
            throw new RejectedExecutionException("Executor is shut down.");
        }
    }
}
