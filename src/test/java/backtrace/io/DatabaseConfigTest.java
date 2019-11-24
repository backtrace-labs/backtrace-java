package backtrace.io;


import backtrace.io.events.OnServerResponseEvent;
import backtrace.io.events.RequestHandler;
import net.jodah.concurrentunit.Waiter;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DatabaseConfigTest {
    @Test
    public void retryLimitTest() {
        // GIVEN
        int RETRY_LIMIT = 3;
        Waiter waiter = new Waiter();

        BacktraceConfig config = new BacktraceConfig("","");
        config.disableDatabase();

        config.setDatabaseRetryLimit(RETRY_LIMIT);

        BacktraceClient client = new BacktraceClient(config);
        BacktraceReport report = new BacktraceReport("test-message");

        // WHEN
        client.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                return BacktraceResult.OnError(data.getReport(), new Exception());
            }
        });

        client.send(report, new OnServerResponseEvent() {
            @Override
            public void onEvent(BacktraceResult backtraceResult) {
                waiter.resume();
            }
        });

        // THEN
        try {
            waiter.await(10, TimeUnit.SECONDS, RETRY_LIMIT);
        }
        catch (TimeoutException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(RETRY_LIMIT, report.getRetryCounter());
    }

    @Test public void limitedNumberOfRecords() {
        // GIVEN

        // WHEN

        // THEN

    }
}
