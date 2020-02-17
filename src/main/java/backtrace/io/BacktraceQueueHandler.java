package backtrace.io;

import backtrace.io.data.BacktraceData;
import backtrace.io.data.BacktraceReport;
import backtrace.io.events.OnServerResponseEvent;

import java.util.Map;
import java.util.concurrent.TimeUnit;

class BacktraceQueueHandler {
    private BacktraceQueue queue;
    private BacktraceThread thread;

    /**
     * Creates instance of BacktraceQueueHandler
     *
     * @param config Library configuration
     */
    BacktraceQueueHandler(BacktraceConfig config) {
        this.queue = new BacktraceQueue();
        this.thread = BacktraceThread.init(config, queue);
    }

    /**
     * Creates BacktraceMessage based on report and attributes and adds message to queue
     *
     * @param report     Current report which contains information about error
     * @param attributes Custom user attributes
     * @param callback   Event which will be executed after receiving the response
     */
    void send(BacktraceReport report, Map<String, Object> attributes, OnServerResponseEvent callback) {
        BacktraceData backtraceData = new BacktraceData(report, attributes);
        queue.addWithLock(new BacktraceMessage(backtraceData, callback));
    }

    /**
     * Stop Backtrace Thread and wait until last message will be sent
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    void close() throws InterruptedException {
        this.thread.close();
    }

    /**
     * Wait until all messages in queue will be sent
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    void await() throws InterruptedException {
        this.queue.await();
    }

    /**
     * Wait until all messages in queue will be sent
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the {@code timeout} argument
     * @return {@code true} if all messages are sent in passed time and {@code false}
     * if the waiting time elapsed before all messages has been sent
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return this.queue.await(timeout, unit);
    }
}