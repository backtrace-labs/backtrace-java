package backtrace.io;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class BacktraceReportTest {
    private static final String message = "test";
    private static final String attributeName = "attribute-name";
    private static final String attributeValue = "attribute-value";

    @Test public void initReportUsingException() {
        // GIVEN
        Exception exception = new Exception(message);
        // WHEN
        BacktraceReport report = new BacktraceReport(exception);
        // THEN
        Assert.assertNull(report.getMessage());
        Assert.assertTrue(report.getExceptionTypeReport());
        Assert.assertEquals(exception, report.getException());
    }

    @Test
    public void initReportUsingNull() {
        // GIVEN
        String message = null;
        // WHEN
        BacktraceReport report = new BacktraceReport(message);
        // THEN
        Assert.assertNotNull(report);
        Assert.assertNull(report.getMessage());
    }

    @Test
    public void initUsingException() {
        // GIVEN
        String message = "message";
        Exception exception = new Exception(message);
        // WHEN
        BacktraceReport report = new BacktraceReport(exception);
        // THEN
        Assert.assertNotNull(report);
        Assert.assertEquals(exception, report.getException());
        Assert.assertTrue(report.getExceptionTypeReport());
        Assert.assertEquals(exception.getClass().getName(), report.getClassifier());
    }

    @Test public void initReportUsingMessage() {
        // WHEN
        BacktraceReport report = new BacktraceReport(message);
        // THEN
        Assert.assertNull(report.getException());
        Assert.assertEquals(message, report.getMessage());
        Assert.assertFalse(report.getExceptionTypeReport());
    }

    @Test public void initReportUsingMessageAndAttribute() {
        // GIVEN
        Map<String, Object> attributes = new HashMap<String, Object>() {{
            put(attributeName, attributeValue);
        }};

        // WHEN
        BacktraceReport report = new BacktraceReport(message, attributes);

        // THEN
        Assert.assertNotNull(report);
        Assert.assertNotNull(report.getAttachmentPaths());
        Assert.assertEquals(0, report.getAttachmentPaths().size());
    }

    @Test public void initReportUsingMessageAndAttachment() {
        // GIVEN
        List<String> attachments = Arrays.asList("test.txt", "test2.txt");

        // WHEN
        BacktraceReport report = new BacktraceReport(message, attachments);

        // THEN
        Assert.assertNotNull(report);
        Assert.assertNotNull(report.getAttachmentPaths());
        Assert.assertEquals(attachments.size(), report.getAttachmentPaths().size());
    }
}
