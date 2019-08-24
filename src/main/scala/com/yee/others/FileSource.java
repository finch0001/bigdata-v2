package com.yee.others;


import java.io.File;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * A simple connector to consume messages from the local file system.
 * It can be configured to consume files recursively from a given
 * directory, and can handle plain text, gzip, and zip formatted files.
 */
public class FileSource extends PushSource<byte[]> {

    private ExecutorService executor;
    private final BlockingQueue<File> workQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<File> inProcess = new LinkedBlockingQueue<>();
    private final BlockingQueue<File> recentlyProcessed = new LinkedBlockingQueue<>();

    @Override
    public void open(Map<String, Object> config, SourceContext sourceContext) throws Exception {
        FileSourceConfig fileConfig = FileSourceConfig.load(config);
        fileConfig.validate();

        // One extra for the File listing thread, and another for the cleanup thread
        executor = Executors.newFixedThreadPool(fileConfig.getNumWorkers() + 2);
        executor.execute(new FileListingThread(fileConfig, workQueue, inProcess, recentlyProcessed));
        executor.execute(new ProcessedFileThread(fileConfig, recentlyProcessed));

        for (int idx = 0; idx < fileConfig.getNumWorkers(); idx++) {
            executor.execute(new FileConsumerThread(this, workQueue, inProcess, recentlyProcessed));
        }
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
