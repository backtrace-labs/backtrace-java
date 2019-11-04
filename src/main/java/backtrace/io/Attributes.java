package backtrace.io;

import java.util.HashMap;
import java.util.Map;



/**
 * Class instance to get a built-in attributes from current application
 */
public class Attributes {

    /**
     * Get built-in primitive attributes
     */
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * Get built-in complex attributes
     */
    private Map<String, Object> complexAttributes = new HashMap<>();


    /**
     * Create instance of Backtrace Attribute
     *
     * @param report           received Backtrace report
     * @param clientAttributes client's attributes (report and client)
     */
    public Attributes(BacktraceReport report, Map<String, Object> clientAttributes) {
        if (report != null) {
            this.splitClientAttributes(report, clientAttributes);
            this.setExceptionAttributes(report);
        }
    }

    Map<String, Object> getAttributes() {
        return attributes;
    }
    /**
     * Set information about exception (message and classifier)
     *
     * @param report received report
     */
    private void setExceptionAttributes(BacktraceReport report) {
        //there is no information to analyse
        if (report == null) {
            return;
        }
        if (!report.exceptionTypeReport) {
            this.attributes.put("error.message", report.message);
            return;
        }
        this.attributes.put("classifier", report.exception.getClass().getName());
        this.attributes.put("error.message", report.exception.getMessage());
    }


    /**
     * Divide custom user attributes into primitive and complex attributes and add to this object
     *
     * @param report           received report
     * @param clientAttributes client's attributes (report and client)
     */
    private void splitClientAttributes(BacktraceReport report, Map<String, Object> clientAttributes) {
        Map<String, Object> attributes = BacktraceReport.concatAttributes(report, clientAttributes);
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            Object value = entry.getValue();
            Class type = value.getClass();
            if (type.isPrimitive() || value instanceof String || type.isEnum()) {
                this.attributes.put(entry.getKey(), value);
            } else {
                this.complexAttributes.put(entry.getKey(), value);
            }
        }
        // add exception information to Complex attributes.
        if (report.exceptionTypeReport) {
            this.complexAttributes.put("Exception properties", report.exception);
        }
    }

    public Map<String, Object> getAnnotations(){
//        BacktraceLogger.d(LOG_TAG, "Setting annotations");
        Object exceptionMessage = null;

        if (this.attributes != null &&
                this.attributes.containsKey("error.message")) {
            exceptionMessage = this.attributes.get("error.message");
        }
        return Annotations.getAnnotations(exceptionMessage, complexAttributes);
    }
}

