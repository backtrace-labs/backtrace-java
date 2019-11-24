package backtrace.io;

import org.junit.Assert;
import org.junit.Test;

public class ApiSenderTest {
    @Test
    public void sendRequestForInvalidUrl(){
        // GIVEN
        String url = "incorrect-url";
        String expectedExceptionMessage = String.format("no protocol: %s", url);
        BacktraceReport report = new BacktraceReport("message");
        BacktraceData backtraceData = new BacktraceData(report);

        // WHEN
        BacktraceResult result = ApiSender.sendReport(url, backtraceData);

        // THEN
        Assert.assertEquals(BacktraceResultStatus.ServerError, result.getStatus());
        Assert.assertEquals(expectedExceptionMessage, result.getMessage());
        Assert.assertEquals(report, result.getBacktraceReport());
    }
}
