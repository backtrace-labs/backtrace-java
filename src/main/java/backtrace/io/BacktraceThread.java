package backtrace.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;


public class BacktraceThread extends Thread {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(BacktraceThread.class);
    private final static String THREAD_NAME = "backtrace-daemon";
    private Backtrace backtrace;
    private volatile boolean running = true;
    private CountDownLatch closing = new CountDownLatch(1);

    /**
     * Creates new thread for handling and sending error reports passed to queue
     *
     * @param config library configuration
     * @param queue  queue containing error reports that should be sent to the Backtrace console
     */
    private BacktraceThread(BacktraceConfig config, BacktraceQueue queue) {
        super();
        this.backtrace = new Backtrace(config, queue);
    }

    /**
     * Creates, configures and start BacktraceThread which will handle and send error reports passed to queue
     *
     * @param config library configuration
     * @param queue  queue containing error reports that should be sent to the Backtrace console
     */
    static BacktraceThread init(BacktraceConfig config, BacktraceQueue queue) {
        LOGGER.info("Initializing BacktraceThread");
        BacktraceThread thread = new BacktraceThread(config, queue);
        thread.setDaemon(true);
        thread.setName(THREAD_NAME);
        thread.start();
        return thread;
    }

    /**
     * Stop Backtrace Thread and wait until last message will be sent
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    void close() throws InterruptedException {
        LOGGER.info("Closing BacktraceThread");
        this.running = false;
        this.closing.await();
    }

    @Override
    public void run() {
        while (running) {
            backtrace.handleBacktraceMessage();
        }
        this.closing.countDown();
    }
}