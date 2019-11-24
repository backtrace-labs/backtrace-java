package backtrace.io;

import backtrace.io.events.BeforeSendEvent;
import backtrace.io.events.OnServerResponseEvent;
import backtrace.io.events.RequestHandler;
import net.jodah.concurrentunit.Waiter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



public class BacktraceClientTest {
    private final String message = "message";
    private BacktraceClient backtraceClient;
    private BacktraceReport report;

    @Test(expected = NullPointerException.class)
    public void initBacktraceClientWithNullConfig() {
        // WHEN
        new BacktraceClient(null);
    }

    @Before
    public void init(){
        BacktraceConfig config = new BacktraceConfig("url", "token");
        this.backtraceClient = new BacktraceClient(config);
        this.report = new BacktraceReport(message);
    }



}