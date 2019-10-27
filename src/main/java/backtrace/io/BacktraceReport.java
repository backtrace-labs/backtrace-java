package backtrace.io;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BacktraceReport {

    private CountDownLatch status;
    public String message;
    public BacktraceReport(){
        this(null);
    }

    public BacktraceReport(String message){
        status = new CountDownLatch(1);
        this.message = message;
    }

    protected void setAsSent(){
        // TODO: add check for status
        status.countDown();
    }

    public void waitUntilSent(){
        try {
            status.await(20000, TimeUnit.SECONDS);
        }
        catch (Exception e){
            System.out.print("[Main Thread] exception on waiting..");
            System.out.println(e);
        }
    }
}
