package backtrace.io;

import backtrace.io.data.BacktraceData;
import backtrace.io.data.BacktraceReport;
import backtrace.io.http.ApiSender;
import backtrace.io.http.BacktraceResult;
import backtrace.io.http.BacktraceResultStatus;
import backtrace.io.http.HttpException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({URL.class, URLConnection.class, ApiSender.class})
public class ApiSenderTest {
    final private String fileAttachmentName = "test.txt";

    @Test
    public void createHttpException() {
        // WHEN
        HttpException exception = new HttpException(200);

        // THEN
        Assert.assertEquals(200, exception.getHttpStatus());
        Assert.assertNull(exception.getMessage());
    }


    @Before
    public void createFileAttachment() {
        try {
            String content = "This is the content to write into a file";
            File file = new File(fileAttachmentName);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (Exception exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @After
    public void removeFileAttachment() {
        try {
            File file = new File(this.fileAttachmentName);
            file.delete();
        } catch (Exception exception) {
            Assert.fail(exception.getMessage());
        }
    }

    @Test
    public void sendRequestForInvalidUrl() {
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

    @Test
    public void sendReportWithSuccessfulResponse() throws Exception {
        // GIVEN
        String url = "https://backtrace.io/";
        String rxId = "03000000-cdf4-a003-0000-000000000000";
        String jsonSuccessResponse = "{\"response\":\"ok\",\"_rxid\":\"" + rxId + "\"}";
        BacktraceReport backtraceReport = new BacktraceReport("");

        // GIVEN - mock URL and HttpURLConnection
        URL u = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withArguments(url).thenReturn(u);
        HttpURLConnection huc = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(u.openConnection()).thenReturn(huc);
        PowerMockito.when(huc.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        PowerMockito.doNothing().when(huc).connect();
        PowerMockito.when(huc.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        PowerMockito.when(huc.getInputStream()).thenReturn(new ByteArrayInputStream(jsonSuccessResponse.getBytes()));

        // WHEN
        BacktraceResult result = ApiSender.sendReport(url, new BacktraceData(backtraceReport));

        // THEN
        Assert.assertEquals(BacktraceResultStatus.Ok, result.getStatus());
        Assert.assertEquals(backtraceReport, result.getBacktraceReport());
        Assert.assertEquals(rxId, result.getRxId());
    }

    @Test
    public void sendReportWithServerError() throws Exception {
        // GIVEN
        String url = "https://backtrace.io/";
        String jsonErrorResponse = "{\"error\":{\"code\":32768, \"message\":\"malformed request\"}}";
        String message = String.format("%s: %s", HttpURLConnection.HTTP_BAD_REQUEST, jsonErrorResponse);
        BacktraceReport backtraceReport = new BacktraceReport("");

        // GIVEN - mock URL and HttpURLConnection
        URL u = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withArguments(url).thenReturn(u);
        HttpURLConnection huc = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(u.openConnection()).thenReturn(huc);
        PowerMockito.when(huc.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);

        PowerMockito.doNothing().when(huc).connect();
        PowerMockito.when(huc.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        PowerMockito.when(huc.getErrorStream()).thenReturn(new ByteArrayInputStream(jsonErrorResponse.getBytes()));

        // WHEN
        BacktraceResult result = ApiSender.sendReport(url, new BacktraceData(backtraceReport));

        // THEN
        Assert.assertEquals(BacktraceResultStatus.ServerError, result.getStatus());
        Assert.assertEquals(backtraceReport, result.getBacktraceReport());
        Assert.assertEquals(message, result.getMessage());
    }

    @Test
    public void sendAttachmentFile() throws Exception {
        // GIVEN
        String url = "https://backtrace.io/";
        String rxId = "03000000-cdf4-a003-0000-000000000000";
        String jsonSuccessResponse = "{\"response\":\"ok\",\"_rxid\":\"" + rxId + "\"}";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BacktraceReport backtraceReport = new BacktraceReport("", new HashMap<String, Object>() {{
            put("custom-attribute", "custom-value");
        }}, new ArrayList<String>() {{
            add(fileAttachmentName);
        }});

        // GIVEN - mock URL and HttpURLConnection
        URL u = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withArguments(url).thenReturn(u);
        HttpURLConnection huc = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(u.openConnection()).thenReturn(huc);
        PowerMockito.when(huc.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        PowerMockito.doNothing().when(huc).connect();
        PowerMockito.when(huc.getOutputStream()).thenReturn(outputStream);
        PowerMockito.when(huc.getInputStream()).thenReturn(new ByteArrayInputStream(jsonSuccessResponse.getBytes()));

        // WHEN
        BacktraceResult result = ApiSender.sendReport(url, new BacktraceData(backtraceReport));

        // THEN
        Assert.assertEquals(BacktraceResultStatus.Ok, result.getStatus());
        Assert.assertTrue(outputStream.size() > 0);
    }
}
