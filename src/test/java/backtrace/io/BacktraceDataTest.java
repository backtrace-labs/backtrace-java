package backtrace.io;

import org.junit.Assert;
import org.junit.Test;

public class BacktraceDataTest {
    private static final String message = "test";

    @Test public void initBacktraceDataUsingException() {
        // GIVEN
        Exception exception = new Exception(message);
        BacktraceReport report = new BacktraceReport(exception);
        // WHEN
        BacktraceData data = new BacktraceData(report);
        // THEN
        Assert.assertEquals(report, data.getReport());
        Assert.assertNull(data.getAttachments());
    }

    @Test public void initBacktraceDataUsingMessage() {
        // GIVEN
        BacktraceReport report = new BacktraceReport(message);
        // WHEN
        BacktraceData data = new BacktraceData(report);
        // THEN
        Assert.assertEquals(report, data.getReport());
        Assert.assertNull(data.getAttachments());
    }
}
