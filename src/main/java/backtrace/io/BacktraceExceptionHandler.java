package backtrace.io;

import backtrace.io.events.OnServerResponseEventListener;

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
    private boolean blockMainThread;

    private BacktraceExceptionHandler(BacktraceClient client, boolean blockMainThread) {
//        BacktraceLogger.d(LOG_TAG, "BacktraceExceptionHandler initialization"); // TODO:
        this.client = client;
        this.blockMainThread = blockMainThread;
        rootHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    /**
     * Enable catching unexpected exceptions by BacktraceClient
     *
     * @param client current Backtrace client instance
     * @param blockMainThread //TODO:
     *               which will be used to send information about exception
     */
    public static void enable(BacktraceClient client, boolean blockMainThread) {
        Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        new BacktraceExceptionHandler(client, blockMainThread);
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
        OnServerResponseEventListener callback = getCallbackToDefaultHandler(thread, throwable);

        if (throwable instanceof Exception) {
//            BacktraceLogger.e(LOG_TAG, "Sending uncaught exception to Backtrace API", throwable);
            this.client.send(new BacktraceReport((Exception) throwable), callback);
//            BacktraceLogger.d(LOG_TAG, "Uncaught exception sent to Backtrace API");
        }
//        BacktraceLogger.d(LOG_TAG, "Default uncaught exception handler");
        if (!blockMainThread){
            return;
        }

        try {
            signal.await();
        } catch (Exception ex) {
//            BacktraceLogger.e(LOG_TAG, "Exception during waiting for response", ex);
        }
    }

    private OnServerResponseEventListener getCallbackToDefaultHandler(final Thread thread, final Throwable throwable) {
        return new OnServerResponseEventListener() {
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