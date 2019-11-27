package backtrace.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
     * Information about report type. If value is true the BacktraceReport has an error
     */
    private boolean exceptionTypeReport = false;


    /**
     * Report classification
     */
    private String classifier = "";

    /**
     * Report attributes
     */
    private Map<String, Object> attributes;


    /**
     * Custom client message
     */
    private String message;


    /**
     * Report exception
     */
    private Exception exception;


    /**
     * All paths to attachments
     */
    List<String> attachmentPaths;

    /**
     * Current report exception stack
     */
    ArrayList<BacktraceStackFrame> diagnosticStack;

    /**
     * Database retry counter
     */
    private transient AtomicInteger retryCounter;

    /**
     * Sending status (UNSENT, SENT)
     */
    private transient BacktraceReportSendingStatus status;

    /**
     * Create new instance of Backtrace report to sending a report with custom client message
     *
     * @param message Custom client message
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
     * @param message    Custom client message
     * @param attributes Additional information about application state
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
     * @param message         Custom client message
     * @param attachmentPaths Path to all report attachments
     */
    public BacktraceReport(
            String message,
            List<String> attachmentPaths
    ) {
        this(message, null, attachmentPaths);
    }


    /**
     * Creates new instance of Backtrace report to sending a report
     * with custom client message, attributes and attachments
     *
     * @param message         Custom client message
     * @param attributes      Additional information about application state
     * @param attachmentPaths Path to all report attachments
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
     * Creates new instance of Backtrace report to sending a report
     * with application exception
     *
     * @param exception Current exception
     */
    public BacktraceReport(
            Exception exception) {
        this(exception, null, null);
    }

    /**
     * Creates new instance of Backtrace report to sending a report
     * with application exception and attributes
     *
     * @param exception  Current exception
     * @param attributes Additional information about application state
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
     * @param exception       Current exception
     * @param attachmentPaths Path to all report attachments
     */
    public BacktraceReport(
            Exception exception,
            List<String> attachmentPaths) {
        this(exception, null, attachmentPaths);
    }

    /**
     * Creates new instance of Backtrace report to sending a report
     * with application exception, attributes and attachments
     *
     * @param exception       Current exception
     * @param attributes      Additional information about application state
     * @param attachmentPaths Path to all report attachments
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
        this.retryCounter = new AtomicInteger(0);
        if (this.getExceptionTypeReport() && exception != null) {
            this.classifier = exception.getClass().getCanonicalName();
        }
    }

    /**
     * Concat two dictionaries with attributes
     *
     * @param report     Current report
     * @param attributes Attributes to concatenate
     * @return Concatenated map of attributes from report and from passed attributes
     */
    static Map<String, Object> concatAttributes(
            BacktraceReport report, Map<String, Object> attributes) {
        Map<String, Object> reportAttributes = report.attributes != null ? report.getAttributes() : new HashMap<>();
        if (attributes == null) {
            return reportAttributes;
        }
        reportAttributes.putAll(attributes);
        return reportAttributes;
    }

    /**
     * Creates object during deserialization
     * @param in Stream
     * @throws  ClassNotFoundException if the class of a serialized object
     *          could not be found.
     * @throws  IOException if an I/O error occurs.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        status = new BacktraceReportSendingStatus();
        retryCounter = new AtomicInteger(0);
    }

    /**
     * Sets that the report has been sent
     */
    void markAsSent() {
        LOGGER.info("Set report status as sent");
        status.reportSent();
    }

    UUID getUuid() {
        return uuid;
    }

    long getTimestamp() {
        return timestamp;
    }

    void incrementRetryCounter() {
        retryCounter.addAndGet(1);
    }

    public int getRetryCounter() {
        return retryCounter.get();
    }

    public String getMessage() {
        return message;
    }

    public BacktraceReportSendingStatus.SendingStatus getSendingStatus(){
        return this.status.getSendingStatus();
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

    public List<String> getAttachmentPaths() {
        return attachmentPaths;
    }

    @SuppressWarnings("WeakerAccess")
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Blocks current thread until report will be sent
     * @param timeout the maximum time to wait
     * @param unit the time unit of the {@code timeout} argument
     * @throws InterruptedException if the current thread is interrupted
     *         while waiting
     */
    public void await(long timeout, TimeUnit unit) throws InterruptedException {
        LOGGER.info("Wait until the report will be sent");
        status.await(timeout, unit);
    }

    /**
     * Blocks current thread until report will be sent
     * @throws InterruptedException if the current thread is interrupted
     *         while waiting
     */
    public void await() throws InterruptedException {
        LOGGER.info("Wait until the report will be sent");
        status.await();
    }
}
