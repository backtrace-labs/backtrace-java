package backtrace.io;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BacktraceReportSendingStatus {
    enum SendingStatus{
        UNSENT,
        SENT
    }
    private final transient int TIMEOUT = 60 * 60; // 1 hour
    private CountDownLatch counter;
    private SendingStatus status = SendingStatus.UNSENT;

    public BacktraceReportSendingStatus(){
        counter = new CountDownLatch(1);
    }

    public void await() throws InterruptedException{
        this.counter.await(TIMEOUT, TimeUnit.SECONDS);
    }

    public SendingStatus getSendingStatus(){
        return status;
    }

    public void reportSent(){
        this.counter.countDown();
        this.status = SendingStatus.SENT;
    }
}
