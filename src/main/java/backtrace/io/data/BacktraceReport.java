package backtrace.io.data;

import backtrace.io.data.report.BacktraceStackFrame;
import backtrace.io.data.report.BacktraceStackTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BacktraceReport implements Serializable {

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
     * Create new instance of Backtrace report to sending a report with custom client message
     *
     * @param message Custom client message
     */
    public BacktraceReport(
            String message
    ) {
        this(message, null, null, null);
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
        this(message, null, attributes, null);
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
        this(message, null, null, attachmentPaths);
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
        this(message, null, attributes, attachmentPaths);
    }

    /**
     * Creates new instance of Backtrace report to sending a report
     * with application exception
     *
     * @param exception Current exception
     */
    public BacktraceReport(
            Exception exception) {
        this(null, exception, null, null);
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
        this(null, exception, attributes, null);
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
        this(null, exception, null, attachmentPaths);
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
        this(null, exception, attributes, attachmentPaths);
    }

    /**
     * Creates new instance of Backtrace report to sending a report
     * with user message, application exception and attributes
     *
     * @param message         Custom client message
     * @param exception       Current exception
     * @param attributes      Additional information about application state
     */
    public BacktraceReport(
            String message,
            Exception exception,
            Map<String, Object> attributes){
        this(message, exception, attributes, null);
    }

    /**
     * Creates new instance of Backtrace report to sending a report
     * with message, application exception and attachments
     *
     * @param message         Custom client message
     * @param exception       Current exception
     * @param attachmentPaths Path to all report attachments
     */
    public BacktraceReport(
            String message,
            Exception exception,
            List<String> attachmentPaths){
        this(message, exception, null, attachmentPaths);
    }

    /**
     * Creates new instance of Backtrace report to sending a report
     * with message and application exception
     *
     * @param message         Custom client message
     * @param exception       Current exception
     */
    public BacktraceReport(
            String message,
            Exception exception){
        this(message, exception, null, null);
    }

    /**
     * Creates new instance of Backtrace report to sending a report
     * with message, application exception, attributes and attachments
     *
     * @param message         Custom client message
     * @param exception       Current exception
     * @param attributes      Additional information about application state
     * @param attachmentPaths Path to all report attachments
     */
    public BacktraceReport(
            String message,
            Exception exception,
            Map<String, Object> attributes,
            List<String> attachmentPaths) {
        this.setMessage(message, exception);
        this.attributes = attributes == null ? new HashMap<String, Object>() {
        } : attributes;
        this.attachmentPaths = attachmentPaths == null ? new ArrayList<>() : attachmentPaths;
        this.exception = exception;
        this.exceptionTypeReport = exception != null;
        this.diagnosticStack = new BacktraceStackTrace(exception).getStackFrames();
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
            backtrace.io.data.BacktraceReport report, Map<String, Object> attributes) {
        Map<String, Object> reportAttributes = report.attributes != null ? report.getAttributes() : new HashMap<>();
        if (attributes == null) {
            return reportAttributes;
        }
        reportAttributes.putAll(attributes);
        return reportAttributes;
    }

    private void setMessage(String message, Exception exception) {
        if (exception != null) {
            this.message = exception.getMessage();
        }

        if (message != null) {
            this.message = message;
        }
    }

    /**
     * Creates object during deserialization
     *
     * @param in Stream
     * @throws ClassNotFoundException if the class of a serialized object
     *                                could not be found.
     * @throws IOException            if an I/O error occurs.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        retryCounter = new AtomicInteger(0);
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void incrementRetryCounter() {
        retryCounter.addAndGet(1);
    }

    public int getRetryCounter() {
        return retryCounter.get();
    }

    public String getMessage() {
        return message;
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
}
