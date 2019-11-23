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

    public BacktraceClient(BacktraceConfig config) {
        this(config, null);
    }

    public BacktraceClient(BacktraceConfig config, Map<String, Object> attributes) {
        if (config == null){
            throw new NullPointerException("BacktraceConfig is null");
        }
        this.customAttributes = attributes != null ? attributes : new HashMap<>();
        this.config = config;
        this.backtrace = new BacktraceQueueHandler(config);
    }

    /**
     * @param customRequestHandler
     */
    public void setCustomRequestHandler(RequestHandler customRequestHandler) {
        config.setRequestHandler(customRequestHandler);
    }

    /**
     * @param beforeSendEvent
     */
    public void setBeforeSendEvent(BeforeSendEvent beforeSendEvent) {
        config.setBeforeSendEvent(beforeSendEvent);
    }

    /**
     * @param report
     */
    public void send(BacktraceReport report) {
        this.send(report, null);
    }

    /**
     * @param report
     * @param callback
     */
    public void send(BacktraceReport report, OnServerResponseEvent callback) {
        this.backtrace.send(report, this.customAttributes, callback);
    }

    /**
     * @param message
     */
    public void send(String message) {
        this.send(message, null);
    }

    /**
     * @param message
     * @param callback
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
     * @param exception
     * @param callback
     */
    public void send(Exception exception, OnServerResponseEvent callback) {
        this.send(new BacktraceReport(exception), callback);
    }

    /**
     *
     */
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
