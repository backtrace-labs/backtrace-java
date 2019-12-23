package backtrace.io;

import backtrace.io.data.BacktraceReport;
import backtrace.io.events.OnServerResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;


/**
 * Backtrace UncaughtExceptionHandler which will be invoked when a Thread abruptly terminates due
 * to an uncaught exception
 */
public class BacktraceExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(BacktraceExceptionHandler.class);

    private final Thread.UncaughtExceptionHandler rootHandler;
    private final CountDownLatch signal = new CountDownLatch(1);
    private BacktraceClient client;
    private boolean blockThread;

    /**
     * Creates BacktraceExceptionHandler instance with BacktraceClient instance and blockThread flag
     *
     * @param client      Current Backtrace client instance
     * @param blockThread Block thread until it gets a response from the API
     *                    which will be used to send information about exception
     */
    private BacktraceExceptionHandler(BacktraceClient client, boolean blockThread) {
        LOGGER.debug("BacktraceExceptionHandler initialization");
        this.client = client;
        this.blockThread = blockThread;
        rootHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    /**
     * Enables catching unexpected exceptions by BacktraceClient
     *
     * @param client      Current Backtrace client instance
     * @param blockThread Block thread until it gets a response from the API
     *                    which will be used to send information about exception
     */
    static void enable(BacktraceClient client, boolean blockThread) {
        if (Thread.getDefaultUncaughtExceptionHandler() instanceof BacktraceExceptionHandler) {
            // BacktraceExceptionHandler already enabled
            return;
        }
        new BacktraceExceptionHandler(client, blockThread);
    }

    /**
     * Enables catching unexpected exceptions by BacktraceClient
     *
     * @param client Current Backtrace client instance which will be used to send information about exception
     */
    public static void enable(BacktraceClient client) {
        new BacktraceExceptionHandler(client, false);
    }

    /**
     * Disables using BacktraceExceptionHandler and sets default uncaught exception handler
     */
    public static void disable() {
        Thread.UncaughtExceptionHandler threadDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (threadDefaultHandler instanceof BacktraceExceptionHandler) {
            BacktraceExceptionHandler backtraceExceptionHandler = (BacktraceExceptionHandler) threadDefaultHandler;
            Thread.setDefaultUncaughtExceptionHandler(backtraceExceptionHandler.rootHandler);
        }
    }

    /**
     * Called when a thread stops because of an uncaught exception
     *
     * @param thread    Thread that is about to exit
     * @param throwable Uncaught exception
     */
    @Override
    public void uncaughtException(final Thread thread, final Throwable throwable) {
        OnServerResponseEvent callback = getCallbackToDefaultHandler(thread, throwable);

        if (throwable instanceof Exception) {
            LOGGER.error("Sending uncaught exception to Backtrace API", throwable);
            this.client.send(new BacktraceReport((Exception) throwable), callback);
            LOGGER.debug("Uncaught exception sent to Backtrace API");
        }
        LOGGER.debug("Default uncaught exception handler");
        if (!blockThread) {
            return;
        }

        try {
            signal.await();
        } catch (Exception ex) {
            LOGGER.error("Exception during waiting for response", ex);
        }
    }

    /**
     * Returns callback to default uncaught exception handler
     *
     * @param thread    current thread
     * @param throwable error
     * @return callback to default uncaught exception handler
     */
    private OnServerResponseEvent getCallbackToDefaultHandler(final Thread thread, final Throwable throwable) {
        return backtraceResult -> {
            LOGGER.debug("Root handler event callback");
            signal.countDown();
            if (rootHandler != null) {
                rootHandler.uncaughtException(thread, throwable);
            }
        };
    }
}