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
    public void testSomeLibraryMethod() {
        // GIVEN
        int RETRY_LIMIT = 10;
        Waiter waiter = new Waiter();

        BacktraceConfig config = new BacktraceConfig("","");
        config.setDatabaseRetryLimit(RETRY_LIMIT);

        BacktraceClient client = new BacktraceClient(config);
        BacktraceReport report = new BacktraceReport("test-message");
        
        // WHEN
        client.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                waiter.resume();
                return BacktraceResult.OnError(data.getReport(), new Exception());
            }
        });

        client.send(report);

        // THEN
        try {
            waiter.await(5000, TimeUnit.SECONDS, RETRY_LIMIT);
        }
        catch (TimeoutException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(RETRY_LIMIT, report.getRetryCounter());
    }
}
