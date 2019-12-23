package backtrace.io;

import backtrace.io.data.BacktraceData;
import backtrace.io.data.BacktraceReport;
import backtrace.io.events.RequestHandler;
import backtrace.io.http.BacktraceResult;
import net.jodah.concurrentunit.Waiter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UncaughtExceptionTest {

    BacktraceConfig config;
    BacktraceClient client;

    @Before
    public void init() {
        config = new BacktraceConfig("", "");
        config.disableDatabase();
        client = new BacktraceClient(config);
    }

    @Test
    public void testUncaughtExceptionHandler() {
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
        Thread testThread = new Thread() {
            public void run() {
                BacktraceExceptionHandler.enable(client, false);
                throw new RuntimeException("Expected!");
            }
        };
        testThread.start();

        // THEN
        try {
            testThread.join();
            waiter.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void testUncaughtExceptionHandlerWithBlockingThread() {
        // GIVEN
        final Waiter waiter = new Waiter();
        client.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                waiter.resume();
                return null;
            }
        });

        // WHEN
        Thread testThread = new Thread() {
            public void run() {
                BacktraceExceptionHandler.enable(client);
                throw new NullPointerException("Expected!");
            }
        };
        testThread.start();

        // THEN
        try {
            testThread.join();
            waiter.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void testEnableUncaughtExceptionHandler() {
        // GIVEN
        final Waiter waiter = new Waiter();

        // WHEN
        Thread testThread = new Thread() {
            public void run() {
                // enable BacktraceExceptionHandler
                client.enableUncaughtExceptionsHandler();
                UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();

                waiter.assertNotNull(handler);
                waiter.assertEquals(handler.getClass(), BacktraceExceptionHandler.class);

                client.disableUncaughtExceptionsHandler();
                waiter.resume();
            }
        };
        testThread.start();

        // THEN
        try {
            testThread.join();
            waiter.await(4, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void testEnableAndDisableUncaughtExceptionHandler() {
        // GIVEN
        final Waiter waiter = new Waiter();

        // WHEN
        Thread testThread = new Thread() {
            public void run() {
                // enable BacktraceExceptionHandler
                client.enableUncaughtExceptionsHandler();
                UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
                waiter.assertNotNull(handler);
                waiter.assertEquals(handler.getClass(), BacktraceExceptionHandler.class);

                // disable BacktraceExceptionHandler
                BacktraceExceptionHandler.disable();
                handler = Thread.getDefaultUncaughtExceptionHandler();
                waiter.assertNull(handler);

                waiter.resume();
            }
        };
        testThread.start();

        // THEN
        try {
            testThread.join();
            waiter.await(3000, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException exception) {
            Assert.fail(exception.getMessage());
        }
    }
}
