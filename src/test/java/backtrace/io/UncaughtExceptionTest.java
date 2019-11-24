package backtrace.io;

import backtrace.io.events.RequestHandler;
import net.jodah.concurrentunit.Waiter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UncaughtExceptionTest {

    BacktraceConfig config;
    BacktraceClient client;

    @Before
    public void init(){
        config = new BacktraceConfig("", "");
        config.disableDatabase();
        client = new BacktraceClient(config);
    }

    @Test
    public void testUncaughtExceptionHandler(){
        // GIVEN
        final Waiter waiter = new Waiter();
        client.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                waiter.resume();
                return BacktraceResult.OnSuccess(new BacktraceReport("test"), "test");
            }
        });

        // WHEN
        Thread testThread = new Thread()
        {
            public void run()
            {
                BacktraceExceptionHandler.enable(client, false);
                throw new RuntimeException("Expected!");
            }
        };
        testThread.start();

        // THEN
        try {
            testThread.join();
            waiter.await(2, TimeUnit.SECONDS);
        }
        catch (InterruptedException | TimeoutException exception){
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void testUncaughtExceptionHandlerWithBlockingThread(){
        // GIVEN
        final Waiter waiter = new Waiter();
        final List<Integer> result = new ArrayList<>();
        client.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                waiter.resume();
                return null;
            }
        });

        // WHEN
        Thread testThread = new Thread()
        {
            public void run()
            {
                BacktraceExceptionHandler.enable(client);
                throw new NullPointerException("Expected!");
            }
        };
        testThread.start();

        // THEN
        try {
            testThread.join();
            waiter.await(2, TimeUnit.SECONDS);
        }
        catch (InterruptedException | TimeoutException exception){
            Assert.fail(exception.getMessage());
        }
    }
}
