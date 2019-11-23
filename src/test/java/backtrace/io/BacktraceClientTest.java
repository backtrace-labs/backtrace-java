package backtrace.io;

import backtrace.io.events.RequestHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class BacktraceClientTest {
    private final String message = "message";

    @Test(expected = NullPointerException.class)
    public void initBacktraceClientWithNullConfig() {
        // WHEN
        new BacktraceClient(null);
    }

    @Test
    public void useRequestHandler(){
        // GIVEN
        BacktraceConfig config = new BacktraceConfig("url", "token");
        BacktraceClient backtraceClient = new BacktraceClient(config);
        final BacktraceReport report = new BacktraceReport(message);
        final LinkedList<Integer> result = new LinkedList<>();
        RequestHandler customRequestHandler = new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                result.add(1);
                return BacktraceResult.OnSuccess(report, "");
            }
        };
        // WHEN
        backtraceClient.setCustomRequestHandler(customRequestHandler);
        backtraceClient.send(report);

        // THEN
        try {
            report.await(1000, TimeUnit.SECONDS);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(1, result.getFirst().intValue());
    }

}
