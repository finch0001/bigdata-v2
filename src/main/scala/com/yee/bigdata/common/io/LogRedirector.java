package com.yee.bigdata.common.io;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.List;

/**
 * Class used to redirect output read from a stream to a logger
 */
public class LogRedirector implements Runnable {

    private static final long MAX_ERR_LOG_LINES_FOR_RPC = 1000;

    public interface LogSourceCallback {
        boolean isAlive();
    }

    private final Logger logger;
    private final BufferedReader in;
    private final LogSourceCallback callback;
    private List<String> errLogs;
    private int numErrLogLines = 0;

    public LogRedirector(InputStream in, Logger logger, LogSourceCallback callback) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.callback = callback;
        this.logger = logger;
    }

    public LogRedirector(InputStream in, Logger logger, List<String> errLogs,
                         LogSourceCallback callback) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.errLogs = errLogs;
        this.callback = callback;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            String line = null;
            while ((line = in.readLine()) != null) {
                logger.info(line);
                if (errLogs != null) {
                    if (numErrLogLines++ < MAX_ERR_LOG_LINES_FOR_RPC) {
                        errLogs.add(line);
                    }
                }
            }
        } catch (IOException e) {
            if (callback.isAlive()) {
                logger.warn("I/O error in redirector thread.", e);
            } else {
                // When stopping the process we are redirecting from,
                // the streams might be closed during reading.
                // We should not log the related exceptions in a visible level
                // as they might mislead the user.
                logger.debug("I/O error in redirector thread while stopping the remote driver", e);
            }
        } catch (Exception e) {
            logger.warn("Error in redirector thread.", e);
        }
    }

    /**
     * Start the logredirector in a new thread
     * @param name name of the new thread
     * @param redirector redirector to start
     */
    public static void redirect(String name, LogRedirector redirector) {
        Thread thread = new Thread(redirector);
        thread.setName(name);
        thread.setDaemon(true);
        thread.start();
    }

}
