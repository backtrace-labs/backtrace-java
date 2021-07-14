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

    @Before
    public void init() {
        config = new BacktraceConfig("", "");
        config.disableDatabase();
    }

    @Test
    public void testUncaughtExceptionHandler() throws InterruptedException {
        // GIVEN
        final Waiter waiter = new Waiter();
        final BacktraceClient client = new BacktraceClient(config);
        client.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                waiter.resume();
                return BacktraceResult.onSuccess(new BacktraceReport("test"), "test");
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
            waiter.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException exception) {
            Assert.fail(exception.getMessage());
        } finally {
            client.close();
            testThread.interrupt();
        }
    }

    @Test
    public void testUncaughtExceptionHandlerWithBlockingThread() throws InterruptedException {
        // GIVEN
        final Waiter waiter = new Waiter();
        final BacktraceClient client = new BacktraceClient(config);
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
            waiter.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException exception) {
            Assert.fail(exception.getMessage());
        } finally {
            client.close();
            testThread.interrupt();
        }
    }

    @Test
    public void testEnableUncaughtExceptionHandler() throws InterruptedException {
        // GIVEN
        final Waiter waiter = new Waiter();
        final BacktraceClient client = new BacktraceClient(config);
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
        } finally {
            client.close();
            testThread.interrupt();
        }
    }

    @Test
    public void testEnableAndDisableUncaughtExceptionHandler() throws InterruptedException {
        // GIVEN
        final Waiter waiter = new Waiter();
        final BacktraceClient client = new BacktraceClient(config);
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
            waiter.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException exception) {
            Assert.fail(exception.getMessage());
        } finally {
            client.close();
            testThread.interrupt();
        }
    }
}
