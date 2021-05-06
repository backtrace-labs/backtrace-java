package backtrace.io.data;

import backtrace.io.BacktraceClient;
import backtrace.io.BacktraceConfig;
import backtrace.io.events.RequestHandler;
import backtrace.io.http.BacktraceResult;
import net.jodah.concurrentunit.Waiter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BacktraceClientThreadsInformationTest {
    private final static String URL = "https://backtrace.io/";

    @Test
    public void gatherInformationAboutAllThreadsFromBacktraceClient() throws TimeoutException, InterruptedException {
        // GIVEN
        final Waiter waiter = new Waiter();
        final BacktraceConfig config = new BacktraceConfig(URL);
        config.setGatherAllThreads(true);

        final BacktraceClient client = new BacktraceClient(config);
        client.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                System.out.println("on REQUEST WAT");
                waiter.assertTrue(data.getThreadInformationMap().size() > 1);
                waiter.resume();
                return BacktraceResult.onSuccess(data.getReport(), "");
            }
        });

        // WHEN
        System.out.println("WAT");
        client.send("test-message");
        // THEN
        try {
            System.out.println("AWAIT");
            waiter.await(50, TimeUnit.SECONDS);
            System.out.println("AWAIT2");
        } catch (Exception exception) {
            System.out.println("FAIL");
            waiter.fail(exception);
        } finally {
            System.out.println("FINALLY");
            client.close();
            System.out.println("AFTER CLOSE");
        }
    }

    @Test
    public void gatherInformationAboutMainThreadFromBacktraceClient() throws TimeoutException, InterruptedException {
        // GIVEN
        final Waiter waiter = new Waiter();
        final BacktraceConfig config = new BacktraceConfig(URL);
        config.setGatherAllThreads(false);

        final BacktraceClient client = new BacktraceClient(config);
        client.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                waiter.assertEquals(1, data.getThreadInformationMap().size());
                waiter.resume();
                return BacktraceResult.onSuccess(data.getReport(), "");
            }
        });

        // WHEN
        client.send("test-message");

        // THEN
        try {
            waiter.await(50, TimeUnit.SECONDS);
        } catch (Exception exception) {
            waiter.fail(exception);
        } finally {
            client.close();
        }
    }
}