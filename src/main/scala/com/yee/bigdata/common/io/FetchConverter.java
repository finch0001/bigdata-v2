package com.yee.bigdata.common.io;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public abstract class FetchConverter extends PrintStream {

    protected volatile boolean queryfound;
    protected volatile boolean fetchStarted;

    public FetchConverter(OutputStream out, boolean autoFlush, String encoding)
            throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }

    public void foundQuery(boolean queryfound) {
        this.queryfound = queryfound;
    }

    public void fetchStarted() {
        fetchStarted = true;
    }

    public void println(String out) {
        if (byPass()) {
            printDirect(out);
        } else {
            process(out);
        }
    }

    protected final void printDirect(String out) {
        super.println(out);
    }

    protected final boolean byPass() {
        return !queryfound || !fetchStarted;
    }

    protected abstract void process(String out);

    protected abstract void processFinal();

    @Override
    public void flush() {
        if (byPass()) {
            super.flush();
        }
    }

    public void fetchFinished() {
        if (!byPass()) {
            processFinal();
        }
        super.flush();
        fetchStarted = false;
    }
}
