package backtrace.io.http;

import backtrace.io.data.BacktraceData;
import backtrace.io.data.BacktraceReport;
import backtrace.io.helpers.BacktraceSerializeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Class for sending and processing HTTP request
 */
public class ApiSender {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(ApiSender.class);

    /**
     * Send HTTP request for certain url server with information about device, error, attachments
     *
     * @param serverUrl server http address to which the request will be sent
     * @return information from the server about the result of processing the request
     */
    public static BacktraceResult sendReport(String serverUrl, BacktraceData backtraceData) {

        String json = BacktraceSerializeHelper.toJson(backtraceData);
        BacktraceReport report = backtraceData.getReport();
        List<String> attachments = backtraceData.getAttachments();

        return sendReport(serverUrl, json, report, attachments);
    }

    private static BacktraceResult sendReport(String serverUrl, String json, BacktraceReport report, List<String> attachments) {
        HttpURLConnection urlConnection = null;
        BacktraceResult result;
        System.out.println("11111111");
        try {
            urlConnection = getUrlConnection(serverUrl);
            System.out.println("222222");
            LOGGER.debug("HttpURLConnection successfully initialized");
            System.out.println("333333");
            DataOutputStream request = new DataOutputStream(urlConnection.getOutputStream());
            System.out.println("4444");
            backtrace.io.http.MultiFormRequestHelper.addJson(request, json);
            backtrace.io.http.MultiFormRequestHelper.addFiles(request, attachments);
            backtrace.io.http.MultiFormRequestHelper.addEndOfRequest(request);

            request.flush();
            request.close();

            int statusCode = urlConnection.getResponseCode();
            LOGGER.debug("Received response status from Backtrace API for HTTP request is: " + statusCode);

            if (statusCode == HttpURLConnection.HTTP_OK) {
                result = handleSuccessResponse(urlConnection, report);
            } else {
                throw new HttpException(statusCode, String.format("%s: %s",
                        Integer.toString(statusCode), getErrorMessage(urlConnection)));
            }

        } catch (Exception e) {
            LOGGER.error("Sending HTTP request failed to Backtrace API", e);
            result = BacktraceResult.onError(report, e);
        } finally {
            if (urlConnection != null) {
                try {
                    urlConnection.disconnect();
                    LOGGER.debug("Disconnecting HttpUrlConnection successful");
                } catch (Exception e) {
                    LOGGER.error("Disconnecting HttpUrlConnection failed", e);
                    result = BacktraceResult.onError(report, e);
                }
            }
        }
        return result;
    }

    private static BacktraceResult handleSuccessResponse(HttpURLConnection urlConnection, BacktraceReport report) throws IOException {
        BacktraceResult result = BacktraceSerializeHelper.fromJson(getResponse(urlConnection), BacktraceResult.class);
        result.setStatus(BacktraceResultStatus.Ok);
        result.setBacktraceReport(report);
        return result;
    }

    private static String getErrorMessage(HttpURLConnection urlConnection) throws IOException {
        String message = getResponse(urlConnection);
        message = message.equals("") ? urlConnection.getResponseMessage() : message;
        return message;
    }

    private static HttpURLConnection getUrlConnection(String serverUrl) throws IOException {
        URL url = new URL(serverUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setUseCaches(false);

        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);

        urlConnection.setChunkedStreamingMode(128 * 1024);
        urlConnection.setRequestProperty("Connection", "Keep-Alive");
        urlConnection.setRequestProperty("Cache-Control", "no-cache");

        urlConnection.setRequestProperty("Content-Type",
                backtrace.io.http.MultiFormRequestHelper.getContentType());

        return urlConnection;
    }

    /**
     * Read response message from HTTP response
     *
     * @param urlConnection current HTTP connection
     * @return response from HTTP request
     * @throws IOException
     */
    private static String getResponse(HttpURLConnection urlConnection) throws IOException {
        LOGGER.debug("Reading response from HTTP request");

        InputStream inputStream;
        if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            inputStream = urlConnection.getInputStream();
        } else {
            inputStream = urlConnection.getErrorStream();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                inputStream));

        StringBuilder responseSB = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            responseSB.append(line);
        }
        br.close();
        return responseSB.toString();
    }
}