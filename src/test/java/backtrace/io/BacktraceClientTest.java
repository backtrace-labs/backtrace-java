package backtrace.io;

import org.junit.Test;


public class BacktraceClientTest {

    @Test(expected = NullPointerException.class)
    public void initBacktraceClientWithNullConfig() {
        // WHEN
        new BacktraceClient(null);
    }

}