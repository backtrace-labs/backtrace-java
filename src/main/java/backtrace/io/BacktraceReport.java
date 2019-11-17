package backtrace.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
     * Get information about report type. If value is true the BacktraceReport has an error
     */
    Boolean exceptionTypeReport = false;

    /**
     * Get a report classification
     */
    String classifier = "";

    /**
     * Get an report attributes
     */
    Map<String, Object> attributes;

    /**
     * Get a custom client message
     */
    public String message;

    /**
     * Get a report exception
     */
    public Exception exception;

    /**
     * Get all paths to attachments
     */
    List<String> attachmentPaths;

    /**
     * Current report exception stack
     */
    ArrayList<StackFrame> diagnosticStack;

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
        this.attachmentPaths = attachmentPaths == null ? new ArrayList<String>() : attachmentPaths;
        this.exception = exception;
        this.exceptionTypeReport = exception != null;
        this.diagnosticStack = new StackTrace(exception).getStackFrames();
        this.status = new CountDownLatch(1);
        if (this.exceptionTypeReport && exception != null) {
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
        Map<String, Object> reportAttributes = report.attributes != null ? report.attributes :
                new HashMap<String, Object>();
        if (attributes == null) {
            return reportAttributes;
        }
        reportAttributes.putAll(attributes);
        return reportAttributes;
    }


    UUID getUuid() {
        return uuid;
    }

    ///////////////////////


    private transient CountDownLatch status;


    long getTimestamp() {
        return timestamp;
    }

    protected void setAsSent(){
        // TODO: add check for status
        status.countDown();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        status = new CountDownLatch(1);
    }

    public void waitUntilSent(){
        try {
            status.await(20000, TimeUnit.SECONDS);
        }
        catch (Exception e){
            System.out.print("[Main Thread] exception on waiting..");
            System.out.println(e);
        }
    }
}
