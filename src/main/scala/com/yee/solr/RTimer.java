package com.yee.solr;


import java.util.concurrent.TimeUnit;

/** A simple timer.
 *
 * RTimers are started automatically when instantiated.
 *
 * @since solr 1.3
 *
 */
public class RTimer {

    public static final int STARTED = 0;
    public static final int STOPPED = 1;
    public static final int PAUSED = 2;

    protected int state;
    private TimerImpl timerImpl;
    private double time;
    private double culmTime;

    protected interface TimerImpl {
        void start();
        double elapsed();
    }

    private static class NanoTimeTimerImpl implements TimerImpl {
        private long start;
        public void start() {
            start = System.nanoTime();
        }
        public double elapsed() {
            return TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        }
    }

    protected TimerImpl newTimerImpl() {
        return new NanoTimeTimerImpl();
    }

    public RTimer() {
        time = 0;
        culmTime = 0;
        timerImpl = newTimerImpl();
        timerImpl.start();
        state = STARTED;
    }

    /** Stop this timer */
    public double stop() {
        assert state == STARTED || state == PAUSED;
        time = culmTime;
        if(state == STARTED)
            time += timerImpl.elapsed();
        state = STOPPED;
        return time;
    }

    public void pause() {
        assert state == STARTED;
        culmTime += timerImpl.elapsed();
        state = PAUSED;
    }

    public void resume() {
        if(state == STARTED)
            return;
        assert state == PAUSED;
        state = STARTED;
        timerImpl.start();
    }

    /** Get total elapsed time for this timer. */
    public double getTime() {
        if (state == STOPPED) return time;
        else if (state == PAUSED) return culmTime;
        else {
            assert state == STARTED;
            return culmTime + timerImpl.elapsed();
        }
    }
}
