package backtrace.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Class instance to get a built-in attributes from current application
 */
class Attributes {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(Attributes.class);
    /**
     * Gets built-in primitive attributes
     */
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * Gets built-in complex attributes
     */
    private Map<String, Object> complexAttributes = new HashMap<>();


    /**
     * Creates instance of Backtrace Attribute
     *
     * @param report           Received Backtrace report
     * @param clientAttributes Client's attributes (report and client)
     */
    Attributes(BacktraceReport report, Map<String, Object> clientAttributes) {
        if (report != null) {
            this.splitClientAttributes(report, clientAttributes);
            this.setExceptionAttributes(report);
        }
    }

    Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Sets information about exception (message and classifier)
     *
     * @param report Received report
     */
    private void setExceptionAttributes(BacktraceReport report) {
        //there is no information to analyse
        if (report == null) {
            return;
        }
        if (!report.getExceptionTypeReport()) {
            this.attributes.put("error.message", report.getMessage());
            return;
        }
        this.attributes.put("classifier", report.getException().getClass().getName());
        this.attributes.put("error.message", report.getException().getMessage());
    }


    /**
     * Divides custom user attributes into primitive and complex attributes and add to this object
     *
     * @param report           Received report
     * @param clientAttributes Client's attributes (report and client)
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
        if (report.getExceptionTypeReport()) {
            this.complexAttributes.put("Exception properties", report.getException());
        }
    }

    /**
     * Sets exception message based on attributes and returns annotations based on complex attributes
     * @return Annotations based on complex attributes
     */
    // TODO: remove setter from get method
    Map<String, Object> getAnnotations() {
        LOGGER.debug("Setting annotations");
        Object exceptionMessage = null;

        if (this.attributes != null && this.attributes.containsKey("error.message")) {
            exceptionMessage = this.attributes.get("error.message");
        }
        return Annotations.getAnnotations(exceptionMessage, this.complexAttributes);
    }
}

