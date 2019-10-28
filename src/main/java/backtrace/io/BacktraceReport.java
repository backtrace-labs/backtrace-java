package backtrace.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BacktraceReport implements Serializable {


    private long timestamp;
    public String message;

    private transient CountDownLatch status;
    public BacktraceReport(){
        this(null);
    }

    public BacktraceReport(String message){
        status = new CountDownLatch(1);
        this.message = message;
    }


    public long getTimestamp() {
        return timestamp;
    }

    protected void setAsSent(){
        // TODO: add check for status
        status.countDown();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        status = new CountDownLatch(1);
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
