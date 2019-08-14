package com.yee.bigdata.common.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper for Thread that sets things up nicely
 */
public class WorkerThread extends Thread {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static WorkerThread daemon(final String name, Runnable runnable) {
        return new WorkerThread(name, runnable, true);
    }

    public static WorkerThread nonDaemon(final String name, Runnable runnable) {
        return new WorkerThread(name, runnable, false);
    }

    public WorkerThread(final String name, boolean daemon) {
        super(name);
        configureThread(name, daemon);
    }

    public WorkerThread(final String name, Runnable runnable, boolean daemon) {
        super(runnable, name);
        configureThread(name, daemon);
    }

    private void configureThread(final String name, boolean daemon) {
        setDaemon(daemon);
        setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                log.error("Uncaught exception in thread '{}':", name, e);
            }
        });
    }

}
