package backtrace.io.http;

import backtrace.io.data.BacktraceReport;
import com.google.gson.annotations.SerializedName;


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
    private BacktraceResult(BacktraceReport report, String message, BacktraceResultStatus status) {
        setBacktraceReport(report);
        setStatus(status);
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
    public static BacktraceResult OnError(BacktraceReport report, Exception exception) {
        return new BacktraceResult(
                report, exception.getMessage(),
                BacktraceResultStatus.ServerError);
    }

    /**
     * Returns result when the report was successfully sent
     *
     * @param report  executed report
     * @param message message from Backtrace API
     * @return BacktraceResult with message from Backtrace API
     */
    public static BacktraceResult OnSuccess(BacktraceReport report, String message) {
        return new BacktraceResult(report, message, BacktraceResultStatus.Ok);
    }
}