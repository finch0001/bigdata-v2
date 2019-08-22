package com.yee.bigdata.common.thread;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to run a Call with a set of {@link CallWrapper}
 */
public class CallRunner {

    /**
     * Helper {@link Callable} that also declares the type of exception it will throw, to help with
     * type safety/generics for java
     * @param <V> value type returned
     * @param <E> type of check exception thrown
     */
    public static interface CallableThrowable<V, E extends Exception> extends Callable<V> {
        @Override
        public V call() throws E;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CallRunner.class);

    private CallRunner() {
        // no ctor for util class
    }

    public static <V, E extends Exception, T extends CallableThrowable<V, E>> V run(T call,
                                                                                    CallWrapper... wrappers) throws E {
        try {
            for (CallWrapper wrap : wrappers) {
                wrap.before();
            }
            return call.call();
        } finally {
            // have to go in reverse, to match the before logic
            for (int i = wrappers.length - 1; i >= 0; i--) {
                try {
                    wrappers[i].after();
                } catch (Exception e) {
                    LOGGER.error("Failed to complete wrapper " + wrappers[i], e);
                }
            }
        }
    }

}