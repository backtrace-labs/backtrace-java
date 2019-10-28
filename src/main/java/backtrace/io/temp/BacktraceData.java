package backtrace.io.temp;



import backtrace.io.temp.BacktraceReport;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;


/**
 * Serializable Backtrace API data object
 */
public class BacktraceData {

    private static transient String LOG_TAG = BacktraceData.class.getSimpleName();

    /**
     * 16 bytes of randomness in human readable UUID format
     * server will reject request if uuid is already found
     */
    @SerializedName("uuid")
    public String uuid;

    /**
     * UTC timestamp in seconds
     */
    @SerializedName("timestamp")
    public long timestamp;

    /**
     * Name of programming language/environment this error comes from.
     */
    @SerializedName("lang")
    public final String lang = "java";

    /**
     * Version of programming language/environment this error comes from.
     */
    @SerializedName("langVersion")
    public String langVersion;

    /**
     * Name of the client that is sending this error report.
     */
    @SerializedName("agent")
    public final String agent = "backtrace-java";

    /**
     * Version of the android library
     */
    @SerializedName("agentVersion")
    public String agentVersion;

    /**
     * Get built-in attributes
     */
    @SerializedName("attributes")
    public Map<String, Object> attributes;

    /**
     * Application thread details
     */
    @SerializedName("threads")
    private Map<String, ThreadInformation> threadInformationMap;

    /**
     * Get a main thread name
     */
    @SerializedName("mainThread")
    public String mainThread;

    /**
     * Get a report classifiers. If user send custom message, then variable should be null
     */
    @SerializedName("classifiers")
    public String[] classifiers;

    /**
     * Current host environment variables
     */
    @SerializedName("annotations")
    public Map<String, Object> annotations;


    @SerializedName("sourceCode")
    public Map<String, SourceCode> sourceCode;

    /**
     * Current BacktraceReport
     */
    public transient BacktraceReport report;


    /**
     * Create instance of report data
     *
     * @param report           current report
     * @param clientAttributes attributes which should be added to BacktraceData object
     */
    public BacktraceData(BacktraceReport report, Map<String, Object> clientAttributes) {
        if (report == null) {
            return;
        }
        this.report = report;

        setReportInformation();
        setThreadsInformation();
        setAttributes(clientAttributes);
    }

    /**
     * Get absolute paths to report attachments
     *
     * @return paths to attachments
     */
    // TODO:
//    public List<String> getAttachments() {
//        return FileHelper.filterOutFiles(report.attachmentPaths);
//    }

    /***
     * Set annotations object
     * @param complexAttributes
     */
    private void setAnnotations(Map<String, Object> complexAttributes) {

        Object exceptionMessage = null;

        if (this.attributes != null &&
                this.attributes.containsKey("error.message")) {
            exceptionMessage = this.attributes.get("error.message");
        }
//        this.annotations = Annotations.getAnnotations(exceptionMessage, complexAttributes); // TODO:
    }

    /**
     * Set attributes and add complex attributes to annotations
     *
     * @param clientAttributes
     */
    private void setAttributes(Map<String, Object> clientAttributes) {
        // TODO:
//
//        BacktraceAttributes backtraceAttributes = new BacktraceAttributes(this.report,
//                clientAttributes);
//        this.attributes = backtraceAttributes.attributes;
//
//        DeviceAttributesHelper deviceAttributesHelper = new DeviceAttributesHelper(this.context);
//        this.attributes.putAll(deviceAttributesHelper.getDeviceAttributes());
//
//        setAnnotations(backtraceAttributes.getComplexAttributes());
    }

    /**
     * Set report information such as report identifier (UUID), timestamp, classifier
     */
    private void setReportInformation() {
        // TODO:
//        uuid = report.uuid.toString();
//        timestamp = report.timestamp;
//        classifiers = report.exceptionTypeReport ? new String[]{report.classifier} : null;
//        langVersion = System.getProperty("java.version"); //TODO: Fix problem with read Java version
//        agentVersion = BuildConfig.VERSION_NAME;
    }

    /**
     * Set information about all threads
     */
    private void setThreadsInformation() {
        // TODO:
//        ThreadData threadData = new ThreadData(report.diagnosticStack);
//        this.mainThread = threadData.getMainThread();
//        this.threadInformationMap = threadData.threadInformation;
//        SourceCodeData sourceCodeData = new SourceCodeData(report.diagnosticStack);
//        this.sourceCode = sourceCodeData.data.isEmpty() ? null : sourceCodeData.data;
    }
}