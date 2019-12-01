package backtrace.io;


import backtrace.io.events.BeforeSendEvent;
import backtrace.io.events.OnServerResponseEvent;
import backtrace.io.events.RequestHandler;

import java.util.HashMap;
import java.util.Map;

public class BacktraceClient {
    private BacktraceQueueHandler backtrace;
    private BacktraceConfig config;
    private final Map<String, Object> customAttributes;

    /**
     * Creates Backtrace client instance with BacktraceConfig
     * @param config Library configuration
     */
    public BacktraceClient(BacktraceConfig config) {
        this(config, null);
    }

    /**
     * Creates Backtrace client instance with BacktraceConfig and custom attributes
     * @param config Library configuration
     * @param attributes Custom attributes which will be attached to each report
     */
    public BacktraceClient(BacktraceConfig config, Map<String, Object> attributes) {
        if (config == null){
            throw new NullPointerException("BacktraceConfig is null");
        }
        this.customAttributes = attributes != null ? attributes : new HashMap<>();
        this.config = config;
        this.backtrace = new BacktraceQueueHandler(config);
    }

    public void setApplicationVersion(String version){
        this.customAttributes.put("version", version);
    }

    public void setApplicationName(String applicationName){
        this.customAttributes.put("application", applicationName);
    }

    /**
     * Sets the request which will be executed instead of the default error sending to the Backtrace API
     * @param customRequestHandler Custom event which will be executed the default error sending to the Backtrace API
     */
    public void setCustomRequestHandler(RequestHandler customRequestHandler) {
        config.setRequestHandler(customRequestHandler);
    }

    /**
     * Sets the event which will be executed before sending the error
     * @param beforeSendEvent Custom event which will be executed before sending the error
     */
    public void setBeforeSendEvent(BeforeSendEvent beforeSendEvent) {
        config.setBeforeSendEvent(beforeSendEvent);
    }

    /**
     * Sends a report to Backtrace API
     * @param report Error report which will be sent
     */
    public void send(BacktraceReport report) {
        this.send(report, null);
    }

    /**
     * Sends a report to Backtrace API and executes callback when receives a response
     * @param report Error report which will be sent
     * @param callback Event which will be executed after receiving a response
     */
    public void send(BacktraceReport report, OnServerResponseEvent callback) {
        this.backtrace.send(report, this.customAttributes, callback);
    }

    /**
     * Sends a message to Backtrace API
     * @param message Text message
     */
    public void send(String message) {
        this.send(message, null);
    }

    /**
     * Sends a message to Backtrace API
     * @param message Text message
     * @param callback Event which will be executed after receiving a response
     */
    public void send(String message, OnServerResponseEvent callback) {
        this.send(new BacktraceReport(message), callback);
    }

    /**
     * @param exception
     */
    public void send(Exception exception) {
        this.send(exception, null);
    }

    /**
     * Sends an exception to Backtrace API
     * @param exception Current exception
     * @param callback Event which will be executed after receiving a response
     */
    public void send(Exception exception, OnServerResponseEvent callback) {
        this.send(new BacktraceReport(exception), callback);
    }

    public void enableUncaughtExceptionsHandler() {
        this.enableUncaughtExceptionsHandler(false);
    }

    /**
     * @param blockThread
     */
    public void enableUncaughtExceptionsHandler(boolean blockThread) {
        BacktraceExceptionHandler.enable(this, blockThread);
    }
}
