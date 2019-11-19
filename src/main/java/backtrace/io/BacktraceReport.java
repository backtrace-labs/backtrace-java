package backtrace.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BacktraceReport implements Serializable {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(BacktraceReport.class);
    /**
     * 16 bytes of randomness in human readable UUID format
     * server will reject request if uuid is already found
     */
    private UUID uuid = UUID.randomUUID();

    /**
     * UTC timestamp in seconds
     */
    long timestamp = System.currentTimeMillis() / 1000;


    /**
     * Get information about report type. If value is true the BacktraceReport has an error
     */
    private boolean exceptionTypeReport = false;


    /**
     * Get a report classification
     */
    private String classifier = "";

    /**
     * Get an report attributes
     */
    private Map<String, Object> attributes;



    /**
     * Get a custom client message
     */
    private String message;


    /**
     * Get a report exception
     */
    private Exception exception;

    /**
     * Get all paths to attachments
     */
    List<String> attachmentPaths;

    /**
     * Current report exception stack
     */
    ArrayList<BacktraceStackFrame> diagnosticStack;

    /**
     * Create new instance of Backtrace report to sending a report with custom client message
     *
     * @param message custom client message
     */
    public BacktraceReport(
            String message
    ) {
        this((Exception) null, null, null);
        this.message = message;
    }

    /**
     * Create new instance of Backtrace report to sending a report
     * with custom client message and attributes
     *
     * @param message    custom client message
     * @param attributes additional information about application state
     */
    public BacktraceReport(
            String message,
            Map<String, Object> attributes
    ) {
        this((Exception) null, attributes, null);
        this.message = message;
    }

    /**
     * Create new instance of Backtrace report to sending a report
     * with custom client message, attributes and attachments
     *
     * @param message         custom client message
     * @param attachmentPaths path to all report attachments
     */
    public BacktraceReport(
            String message,
            List<String> attachmentPaths
    ) {
        this(message, null, attachmentPaths);
    }


    /**
     * Create new instance of Backtrace report to sending a report
     * with custom client message, attributes and attachments
     *
     * @param message         custom client message
     * @param attributes      additional information about application state
     * @param attachmentPaths path to all report attachments
     */
    public BacktraceReport(
            String message,
            Map<String, Object> attributes,
            List<String> attachmentPaths
    ) {
        this((Exception) null, attributes, attachmentPaths);
        this.message = message;
    }

    /**
     * Create new instance of Backtrace report to sending a report
     * with application exception
     *
     * @param exception current exception
     */
    public BacktraceReport(
            Exception exception) {
        this(exception, null, null);
    }

    /**
     * Create new instance of Backtrace report to sending a report
     * with application exception and attributes
     *
     * @param exception  current exception
     * @param attributes additional information about application state
     */
    public BacktraceReport(
            Exception exception,
            Map<String, Object> attributes) {
        this(exception, attributes, null);
    }

    /**
     * Create new instance of Backtrace report to sending a report
     * with application exception, attributes and attachments
     *
     * @param exception       current exception
     * @param attachmentPaths path to all report attachments
     */
    public BacktraceReport(
            Exception exception,
            List<String> attachmentPaths) {
        this(exception, null, attachmentPaths);
    }

    /**
     * Create new instance of Backtrace report to sending a report
     * with application exception, attributes and attachments
     *
     * @param exception       current exception
     * @param attributes      additional information about application state
     * @param attachmentPaths path to all report attachments
     */
    public BacktraceReport(
            Exception exception,
            Map<String, Object> attributes,
            List<String> attachmentPaths) {

        this.attributes = attributes == null ? new HashMap<String, Object>() {
        } : attributes;
        this.attachmentPaths = attachmentPaths == null ? new ArrayList<>() : attachmentPaths;
        this.exception = exception;
        this.exceptionTypeReport = exception != null;
        this.diagnosticStack = new BacktraceStackTrace(exception).getStackFrames();
        this.status = new BacktraceReportSendingStatus();
        if (this.getExceptionTypeReport() && exception != null) {
            this.classifier = exception.getClass().getCanonicalName();
        }
    }

    /**
     * Concat two dictionaries with attributes
     *
     * @param report     current report
     * @param attributes attributes to concatenate
     * @return concatenated map of attributes from report and from passed attributes
     */
    static Map<String, Object> concatAttributes(
            BacktraceReport report, Map<String, Object> attributes) {
        Map<String, Object> reportAttributes = report.attributes != null ? report.attributes : new HashMap<>();
        if (attributes == null) {
            return reportAttributes;
        }
        reportAttributes.putAll(attributes);
        return reportAttributes;
    }

    /**
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        status = new BacktraceReportSendingStatus();
    }


    UUID getUuid() {
        return uuid;
    }

    ///////////////////////


    private transient BacktraceReportSendingStatus status;

    public String getMessage() {
        return message;
    }

    long getTimestamp() {
        return timestamp;
    }

    public Exception getException() {
        return exception;
    }

    public boolean getExceptionTypeReport() {
        return exceptionTypeReport;
    }

    public String getClassifier() {
        return classifier;
    }

    void markAsSent(){
        LOGGER.info("Set report status as sent");
        status.reportSent();
    }

    public void await(long timeout, TimeUnit unit) throws InterruptedException{
        LOGGER.info("Wait until the report will be sent");
        status.await(timeout, unit);
    }

    public void await() throws InterruptedException{
        LOGGER.info("Wait until the report will be sent");
        status.await();
    }
}
