package backtrace.io.data.report;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BacktraceReportSendingStatus {
    public enum SendingStatus {
        UNSENT,
        SENT
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final transient int TIMEOUT = 60 * 60; // 1 hour
    private CountDownLatch counter;
    private SendingStatus status = SendingStatus.UNSENT;

    public BacktraceReportSendingStatus() {
        counter = new CountDownLatch(1);
    }

    /**
     * Blocks current thread until report will be sent
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the {@code timeout} argument
     * @throws InterruptedException if the current thread is interrupted
     *                              while waiting
     */
    public void await(long timeout, TimeUnit unit) throws InterruptedException {
        this.counter.await(timeout, unit);
    }

    /**
     * Blocks current thread until report will be sent
     *
     * @throws InterruptedException if the current thread is interrupted
     *                              while waiting
     */
    public void await() throws InterruptedException {
        this.await(TIMEOUT, TimeUnit.SECONDS);
    }

    public SendingStatus getSendingStatus() {
        return status;
    }

    /**
     * Sets that the report has been sent
     */
    public void reportSent() {
        this.counter.countDown();
        this.status = SendingStatus.SENT;
    }
}
