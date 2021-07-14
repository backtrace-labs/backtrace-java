package backtrace.io;

import backtrace.io.data.BacktraceData;
import backtrace.io.data.BacktraceReport;
import backtrace.io.http.ApiSender;
import backtrace.io.http.BacktraceResult;
import backtrace.io.http.BacktraceResultStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({URL.class, URLConnection.class, ApiSender.class})
public class RequestRetryingTest {

    private BacktraceResultStatus serverResultStatus;
    private Integer httpStatusCode;
    private boolean retry;

    public RequestRetryingTest(BacktraceResultStatus serverResultStatus, Integer httpStatusCode, boolean retry) {
        this.serverResultStatus = serverResultStatus;
        this.httpStatusCode = httpStatusCode;
        this.retry = retry;
    }

    @Test
    public void ifRequestShouldBeResent() throws Exception {
        // GIVEN
        String url = "https://backtrace.io/";
        String jsonSuccessResponse = "{\"response\":\"error\",\"_rxid\":\"" + "00000000-0000-0000-0000-000000000000" + "\"}";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BacktraceReport backtraceReport = new BacktraceReport("");

        // GIVEN - mock URL and HttpURLConnection
        URL u = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withArguments(url).thenReturn(u);
        HttpURLConnection huc = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(u.openConnection()).thenReturn(huc);
        PowerMockito.when(huc.getResponseCode()).thenReturn(httpStatusCode);

        PowerMockito.doNothing().when(huc).connect();
        PowerMockito.when(huc.getOutputStream()).thenReturn(outputStream);
        PowerMockito.when(huc.getInputStream()).thenReturn(new ByteArrayInputStream(jsonSuccessResponse.getBytes()));

        // WHEN
        BacktraceResult result = ApiSender.sendReport(url, new BacktraceData(backtraceReport));

        // THEN
        Assert.assertEquals(serverResultStatus, result.getStatus());
        Assert.assertEquals(httpStatusCode.intValue(), result.getHttpStatusCode().intValue());
        Assert.assertEquals(retry, result.shouldRetry());
    }

    @Parameterized.Parameters(name = "{index}: ({0}, {1}) = {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {BacktraceResultStatus.Ok, HttpURLConnection.HTTP_OK, false},
                {BacktraceResultStatus.ServerError, HttpURLConnection.HTTP_BAD_REQUEST, false},
                {BacktraceResultStatus.ServerError, HttpURLConnection.HTTP_FORBIDDEN, false},
                {BacktraceResultStatus.ServerError, HttpURLConnection.HTTP_NOT_FOUND, false},
                {BacktraceResultStatus.ServerError, HttpURLConnection.HTTP_BAD_GATEWAY, true},
                {BacktraceResultStatus.ServerError, HttpURLConnection.HTTP_GATEWAY_TIMEOUT, true},
                {BacktraceResultStatus.ServerError, HttpURLConnection.HTTP_CLIENT_TIMEOUT, true},
                {BacktraceResultStatus.ServerError, HttpURLConnection.HTTP_UNAVAILABLE, true},
                {BacktraceResultStatus.ServerError, HttpURLConnection.HTTP_INTERNAL_ERROR, true}
        });
    }
}
