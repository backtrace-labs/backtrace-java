package backtrace.io;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for sending and processing HTTP request
 */
class ApiSender {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(ApiSender.class);

    /**
     * Send HTTP request for certain url server with information about device, error, attachments
     *
     * @param serverUrl     server http address to which the request will be sent
     * @return information from the server about the result of processing the request
     */
    static BacktraceResult sendReport(String serverUrl, BacktraceData backtraceData) {

        String json = BacktraceSerializeHelper.toJson(backtraceData);
        BacktraceReport report = backtraceData.getReport();
        List<String> attachments = backtraceData.getAttachments();

        HttpURLConnection urlConnection = null;
        BacktraceResult result;

        try {
            urlConnection = getUrlConnection(serverUrl);

            LOGGER.debug("HttpURLConnection successfully initialized");
            DataOutputStream request = new DataOutputStream(urlConnection.getOutputStream());

            MultiFormRequestHelper.addJson(request, json);
            MultiFormRequestHelper.addFiles(request, attachments);
            MultiFormRequestHelper.addEndOfRequest(request);

            request.flush();
            request.close();

            int statusCode = urlConnection.getResponseCode();
            LOGGER.debug("Received response status from Backtrace API for HTTP request is: " + Integer.toString(statusCode));

            if (statusCode == HttpURLConnection.HTTP_OK) {
                result = BacktraceSerializeHelper.fromJson(getResponse(urlConnection), BacktraceResult.class);
                result.setStatus(BacktraceResultStatus.Ok);
                result.setBacktraceReport(report);
            } else {
                String message = getResponse(urlConnection);
                message = (message == null || message.equals("")) ?
                        urlConnection.getResponseMessage() : message;
                throw new HttpException(statusCode, String.format("%s: %s",
                        Integer.toString(statusCode), message));
            }

        } catch (Exception e) {
            LOGGER.error("Sending HTTP request failed to Backtrace API", e);
            result = BacktraceResult.OnError(report, e);
        } finally {
            if (urlConnection != null) {
                try {
                    urlConnection.disconnect();
                    LOGGER.debug("Disconnecting HttpUrlConnection successful");
                } catch (Exception e) {
                    LOGGER.error("Disconnecting HttpUrlConnection failed", e);
                    result = BacktraceResult.OnError(report, e);
                }
            }
        }
        return result;
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
                MultiFormRequestHelper.getContentType());

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