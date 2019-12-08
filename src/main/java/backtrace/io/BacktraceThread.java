package backtrace.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BacktraceThread extends Thread {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(BacktraceThread.class);
    private final static String THREAD_NAME = "backtrace-deamon";
    private Backtrace backtrace;

    /**
     * Creates new thread for handling and sending error reports passed to queue
     * @param config library configuration
     * @param queue queue containing error reports that should be sent to the Backtrace console
     */
    private BacktraceThread(BacktraceConfig config, ConcurrentLinkedQueue<BacktraceMessage> queue) {
        super();
        this.backtrace = new Backtrace(config, queue);
    }

    /**
     * Creates, configures and start BacktraceThread which will handle and send error reports passed to queue
     * @param config library configuration
     * @param queue queue containing error reports that should be sent to the Backtrace console
     */
    static void init(BacktraceConfig config, ConcurrentLinkedQueue<BacktraceMessage> queue) {
        LOGGER.info("Initialize BacktraceThread");
        BacktraceThread thread = new BacktraceThread(config, queue);
        thread.setDaemon(true);
        thread.setName(THREAD_NAME);
        thread.start();
    }

    @Override
    public void run() {
        backtrace.handleBacktraceMessages();
    }
}