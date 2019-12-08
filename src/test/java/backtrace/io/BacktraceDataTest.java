package backtrace.io;

import backtrace.io.data.BacktraceData;
import backtrace.io.data.BacktraceReport;
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
        Assert.assertEquals(0, data.getAttachments().size());
        Assert.assertNotNull(data.getAttachments());
    }

    @Test public void initBacktraceDataUsingString() {
        // GIVEN
        BacktraceReport report = new BacktraceReport(message);
        // WHEN
        BacktraceData data = new BacktraceData(report);
        // THEN
        Assert.assertEquals(report, data.getReport());
        Assert.assertEquals(0, data.getAttachments().size());
        Assert.assertNotNull(data.getAttachments());
    }

    @Test(expected = NullPointerException.class)
    public void tryInitBacktraceDataWithNullReport(){
        // GIVEN
        BacktraceReport report = null;
        // WHEN
        new BacktraceData(report);
    }
}
