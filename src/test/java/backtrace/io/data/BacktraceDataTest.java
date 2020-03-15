package backtrace.io.data;

import backtrace.io.data.report.ThreadInformation;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class BacktraceDataTest {
    private static final String message = "test";

    @Test
    public void initBacktraceDataUsingException() {
        // GIVEN
        final Exception exception = new Exception(message);
        final BacktraceReport report = new BacktraceReport(exception);

        // WHEN
        BacktraceData data = new BacktraceData(report);

        // THEN
        Assert.assertEquals(report, data.getReport());
        Assert.assertEquals(0, data.getAttachments().size());
        Assert.assertNotNull(data.getAttachments());
    }

    @Test
    public void initBacktraceDataUsingString() {
        // GIVEN
        final BacktraceReport report = new BacktraceReport(message);

        // WHEN
        BacktraceData data = new BacktraceData(report);

        // THEN
        Assert.assertEquals(report, data.getReport());
        Assert.assertEquals(0, data.getAttachments().size());
        Assert.assertNotNull(data.getAttachments());
    }

    @Test(expected = NullPointerException.class)
    public void tryInitBacktraceDataWithNullReport() {
        // GIVEN
        final BacktraceReport report = null;

        // WHEN
        new BacktraceData(report);
    }

    @Test
    public void gatherInformationAboutAllThreads(){
        // GIVEN
        final BacktraceReport report = new BacktraceReport("");

        // WHEN
        BacktraceData data = new BacktraceData(report);
        Map<String, ThreadInformation> threadInformationMap = data.getThreadInformationMap();

        // THEN
        Assert.assertTrue(threadInformationMap.size() > 1);
    }

    @Test
    public void gatherInformationOnlyAboutMainThread(){
        // GIVEN
        final boolean allThreads = false;
        final BacktraceReport report = new BacktraceReport("");

        // WHEN
        BacktraceData data = new BacktraceData(report, null, allThreads);
        Map<String, ThreadInformation> threadInformationMap = data.getThreadInformationMap();

        // THEN
        Assert.assertEquals(1, threadInformationMap.size());
    }
}
