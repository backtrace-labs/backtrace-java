package backtrace.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


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
            this.setHardwareAttributes();
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
     * Set machine attributes such as memory, operating system and processor details
     */
    private void setHardwareAttributes() {
        int cpuCores = Runtime.getRuntime().availableProcessors();

        if (cpuCores > 0) {
            this.attributes.put("cpu.count", cpuCores);
        }

        this.attributes.put("uname.sysname", System.getProperty("os.name"));
        this.attributes.put("uname.version", System.getProperty("os.version"));

        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            this.attributes.put("hostname", hostname);
        } catch (UnknownHostException exception) {
            LOGGER.error("Can not get hostname", exception);
        }

        this.attributes.put("guid", generateMachineId());

        this.attributes.put("system.memory.total", Runtime.getRuntime().totalMemory());
        this.attributes.put("system.memory.free", Runtime.getRuntime().freeMemory());
        this.attributes.put("system.memory.max", Runtime.getRuntime().maxMemory());
    }

    /**
     * Generate a unique identifier that uniquely determines the device
     * @return unique device identifier
     */
    private String generateMachineId() {
        try {
            InetAddress ip = InetAddress.getLocalHost();

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            String macAddress = sb.toString();
            String hex = macAddress.replace(":", "").replace("-", "");
            return UUID.nameUUIDFromBytes(hex.getBytes()).toString();
        } catch (SocketException | UnknownHostException exception) {
            LOGGER.error("Can not get device MAC address", exception);
        }
        return UUID.randomUUID().toString();
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
     *
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

