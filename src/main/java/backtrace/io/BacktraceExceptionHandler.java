package backtrace.io;

import backtrace.io.events.OnServerResponseEvent;

import java.util.concurrent.CountDownLatch;



/**
 * Backtrace UncaughtExceptionHandler which will be invoked when a Thread abruptly terminates due
 * to an uncaught exception
 */
public class BacktraceExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static transient String LOG_TAG = BacktraceExceptionHandler.class.getSimpleName();

    private final Thread.UncaughtExceptionHandler rootHandler;
    private final CountDownLatch signal = new CountDownLatch(1);
    private BacktraceClient client;
    private boolean blockThread;

    private BacktraceExceptionHandler(BacktraceClient client, boolean blockThread) {
//        BacktraceLogger.d(LOG_TAG, "BacktraceExceptionHandler initialization"); // TODO:
        this.client = client;
        this.blockThread = blockThread;
        rootHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    /**
     * Enable catching unexpected exceptions by BacktraceClient
     *
     * @param client current Backtrace client instance
     * @param blockThread //TODO:
     *               which will be used to send information about exception
     */
    static void enable(BacktraceClient client, boolean blockThread) {
        Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        new BacktraceExceptionHandler(client, blockThread);
    }

    /**
     * Enable catching unexpected exceptions by BacktraceClient
     *
     * @param client current Backtrace client instance
     *               which will be used to send information about exception
     */
    public static void enable(BacktraceClient client) {
        new BacktraceExceptionHandler(client, false);
    }

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
     * @param thread    thread that is about to exit
     * @param throwable uncaught exception
     */
    @Override
    public void uncaughtException(final Thread thread, final Throwable throwable) {
        OnServerResponseEvent callback = getCallbackToDefaultHandler(thread, throwable);

        if (throwable instanceof Exception) {
//            BacktraceLogger.e(LOG_TAG, "Sending uncaught exception to Backtrace API", throwable);
            this.client.send(new BacktraceReport((Exception) throwable), callback);
//            BacktraceLogger.d(LOG_TAG, "Uncaught exception sent to Backtrace API");
        }
//        BacktraceLogger.d(LOG_TAG, "Default uncaught exception handler");
        if (!blockThread){
            return;
        }

        try {
            signal.await();
        } catch (Exception ex) {
//            BacktraceLogger.e(LOG_TAG, "Exception during waiting for response", ex);
        }
    }

    private OnServerResponseEvent getCallbackToDefaultHandler(final Thread thread, final Throwable throwable) {
        return new OnServerResponseEvent() {
            @Override
            public void onEvent(BacktraceResult backtraceResult) {
//                BacktraceLogger.d(LOG_TAG, "Root handler event callback");
                signal.countDown();
                if (rootHandler != null) {
                    rootHandler.uncaughtException(thread, throwable);
                }
            }
        };
    }
}