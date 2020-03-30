package backtrace.io.http;

import backtrace.io.data.BacktraceReport;
import com.google.gson.annotations.SerializedName;

import java.net.HttpURLConnection;


/**
 * Send method result
 */
public class BacktraceResult {


    /**
     * Object identifier
     */
    @SerializedName("_rxid")
    @SuppressWarnings({"UnusedDeclaration"})
    private String rxId;


    /**
     * Message
     */
    @SerializedName("response")
    private String message;


    /**
     * Response HTTP status code
     */

    private Integer httpStatusCode;


    /**
     * Result status eg. server error, ok
     */
    private BacktraceResultStatus status;

    /**
     * Current report
     */
    private BacktraceReport backtraceReport;

    /**
     * Create new instance of BacktraceResult
     *
     * @param report  executed report
     * @param message message
     * @param status  result status eg. ok, server error
     */
    private BacktraceResult(BacktraceReport report, String message, BacktraceResultStatus status, Integer responseHttpStatusCode) {
        setBacktraceReport(report);
        setStatus(status);
        setHttpStatusCode(responseHttpStatusCode);
        this.message = message;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public String getRxId() {
        return rxId;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public String getMessage() {
        return message;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public BacktraceResultStatus getStatus() {
        return status;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public BacktraceReport getBacktraceReport() {
        return backtraceReport;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    void setBacktraceReport(BacktraceReport backtraceReport) {
        this.backtraceReport = backtraceReport;
    }

    void setStatus(BacktraceResultStatus status) {
        this.status = status;
    }


    /**
     * Returns result when error occurs while sending data to API
     *
     * @param report    executed report
     * @param exception current exception
     * @return BacktraceResult with exception information
     */
    public static BacktraceResult onError(BacktraceReport report, Exception exception) {
        return BacktraceResult.onError(report, exception, null);
    }

    /**
     * Returns result when error occurs while sending data to API
     *
     * @param report    executed report
     * @param exception current exception
     * @param httpStatusCode returned http status code
     * @return BacktraceResult with exception information
     */
    static BacktraceResult onError(BacktraceReport report, Exception exception, Integer httpStatusCode) {
        return new BacktraceResult(
                report, exception.getMessage(),
                BacktraceResultStatus.ServerError, httpStatusCode);
    }

    /**
     * Returns result when the report was successfully sent
     *
     * @param report  executed report
     * @param message message from Backtrace API
     * @return BacktraceResult with message from Backtrace API
     */
    public static BacktraceResult onSuccess(BacktraceReport report, String message) {
        return new BacktraceResult(report, message, BacktraceResultStatus.Ok, HttpURLConnection.HTTP_OK);
    }

    public boolean shouldRetry() {
        return httpStatusCode == null || httpStatusCode == HttpURLConnection.HTTP_GATEWAY_TIMEOUT ||
                httpStatusCode == HttpURLConnection.HTTP_BAD_GATEWAY || httpStatusCode == HttpURLConnection.HTTP_INTERNAL_ERROR
                || httpStatusCode == HttpURLConnection.HTTP_UNAVAILABLE || httpStatusCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT;
    }
}