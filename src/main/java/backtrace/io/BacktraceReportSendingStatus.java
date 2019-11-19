package backtrace.io;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BacktraceReportSendingStatus {
    enum SendingStatus {
        UNSENT,
        SENT
    }

    private final transient int TIMEOUT = 60 * 60; // 1 hour
    private CountDownLatch counter;
    private SendingStatus status = SendingStatus.UNSENT;

    BacktraceReportSendingStatus() {
        counter = new CountDownLatch(1);
    }

    void await(long timeout, TimeUnit unit) throws InterruptedException {
        this.counter.await(timeout, unit);
    }

    void await() throws InterruptedException {
        this.await(TIMEOUT, TimeUnit.SECONDS);
    }

    public SendingStatus getSendingStatus() {
        return status;
    }

    void reportSent() {
        this.counter.countDown();
        this.status = SendingStatus.SENT;
    }
}
