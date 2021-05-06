package backtrace.io;

import backtrace.io.data.BacktraceData;
import backtrace.io.events.RequestHandler;
import backtrace.io.http.BacktraceResult;
import net.jodah.concurrentunit.Waiter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class BacktraceClientTest {
    private final String THREAD_NAME = "backtrace-daemon";

    @Test(expected = NullPointerException.class)
    public void initBacktraceClientWithNullConfig() {
        // WHEN
        new BacktraceClient(null);
    }

    @Test
    public void closeBacktraceClient() throws InterruptedException {
        // GIVEN
        BacktraceConfig backtraceConfig = new BacktraceConfig("https://backtrace.io/");
        BacktraceClient backtraceClient = new BacktraceClient(backtraceConfig);

        // WHEN
        boolean isBacktraceThreadRunning = isBacktraceThreadRunning();
        backtraceClient.close();

        boolean isBacktraceThreadRunningAfterClose = isBacktraceThreadRunning();

        System.out.println(isBacktraceThreadRunning);
        // THEN
        Assert.assertTrue(isBacktraceThreadRunning);
        Assert.assertFalse(isBacktraceThreadRunningAfterClose);
    }

    @Test
    public void closeBacktraceClientWithSendingReport() throws InterruptedException, TimeoutException {
        // GIVEN
        BacktraceConfig backtraceConfig = new BacktraceConfig("https://backtrace.io/");
        Waiter waiter = new Waiter();
        backtraceConfig.setRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                try {
                    Thread.sleep(1000);
                    waiter.resume();
                } catch (InterruptedException e) {
                    waiter.fail(e);
                }
                return BacktraceResult.onSuccess(data.getReport(), data.getReport().getMessage());
            }
        });
        BacktraceClient backtraceClient = new BacktraceClient(backtraceConfig);

        // WHEN
        boolean isBacktraceThreadRunning = isBacktraceThreadRunning();
        backtraceClient.send("test-message");
        backtraceClient.close();

        boolean isBacktraceThreadRunningAfterClose = isBacktraceThreadRunning();
        waiter.await(5, TimeUnit.SECONDS);

        // THEN
        Assert.assertTrue(isBacktraceThreadRunning);
        Assert.assertFalse(isBacktraceThreadRunningAfterClose);
    }

    private boolean isBacktraceThreadRunning(){
        Set<Thread> threads = Thread.getAllStackTraces().keySet();

        for (Thread t : threads) {
            if (t == null){
                continue;
            }
            if (t.getName().equals(THREAD_NAME)) {
                return true;
            }
        }
        return false;
    }

}