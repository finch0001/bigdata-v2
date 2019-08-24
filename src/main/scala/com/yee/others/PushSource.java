package com.yee.others;


import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Pulsar's Push Source interface. PushSource read data from
 * external sources(database changes, twitter firehose, etc)
 * and publish to a Pulsar topic. The reason its called Push is
 * because PushSources get passed a consumer that they
 * invoke whenever they have data to be published to Pulsar.
 * The lifecycle of a PushSource is to open it passing any config needed
 * by it to initialize(like open network connection, authenticate, etc).
 * A consumer  is then to it which is invoked by the source whenever
 * there is data to be published. Once all data has been read, one can use close
 * at the end of the session to do any cleanup
 */
public abstract class PushSource<T> implements Source<T> {

    private LinkedBlockingQueue<Record<T>> queue;
    private static final int DEFAULT_QUEUE_LENGTH = 1000;

    public PushSource() {
        this.queue = new LinkedBlockingQueue<>(this.getQueueLength());
    }

    @Override
    public Record<T> read() throws Exception {
        return queue.take();
    }

    /**
     * Open connector with configuration
     *
     * @param config initialization config
     * @param sourceContext
     * @throws Exception IO type exceptions when opening a connector
     */
    abstract public void open(Map<String, Object> config, SourceContext sourceContext) throws Exception;

    /**
     * Attach a consumer function to this Source. This is invoked by the implementation
     * to pass messages whenever there is data to be pushed to Pulsar.
     * @param consumer
     */
    public void consume(Record<T> record) {
        try {
            queue.put(record);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get length of the queue that records are push onto
     * Users can override this method to customize the queue length
     * @return queue length
     */
    public int getQueueLength() {
        return DEFAULT_QUEUE_LENGTH;
    }
}
