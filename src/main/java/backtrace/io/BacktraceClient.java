package backtrace.io;


import backtrace.io.data.BacktraceReport;
import backtrace.io.events.BeforeSendEvent;
import backtrace.io.events.OnServerResponseEvent;
import backtrace.io.events.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BacktraceClient {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(BacktraceClient.class);
    private BacktraceQueueHandler backtrace;
    private BacktraceConfig config;
    private final Map<String, Object> customAttributes;

    /**
     * Creates Backtrace client instance with BacktraceConfig
     *
     * @param config Library configuration
     */
    public BacktraceClient(BacktraceConfig config) {
        this(config, null);
    }

    /**
     * Creates Backtrace client instance with BacktraceConfig and custom attributes
     *
     * @param config     Library configuration
     * @param attributes Custom attributes which will be attached to each report
     */
    public BacktraceClient(BacktraceConfig config, Map<String, Object> attributes) {
        if (config == null) {
            throw new NullPointerException("BacktraceConfig is null");
        }
        this.customAttributes = attributes != null ? attributes : new HashMap<>();
        this.config = config;
        this.backtrace = new BacktraceQueueHandler(config);
    }

    /**
     * Set application version, this information will be attached to each of reports as 'version' attribute
     *
     * @param version application version
     */
    public void setApplicationVersion(String version) {
        this.customAttributes.put("version", version);
    }

    /**
     * Set application name, this information will be attached to each of reports as 'application' attribute
     *
     * @param applicationName application name
     */
    public void setApplicationName(String applicationName) {
        this.customAttributes.put("application", applicationName);
    }

    /**
     * Sets the request which will be executed instead of the default error sending to the Backtrace Console
     *
     * @param customRequestHandler Custom event which will be executed the default error sending to the Backtrace API
     */
    public void setCustomRequestHandler(RequestHandler customRequestHandler) {
        config.setRequestHandler(customRequestHandler);
    }

    /**
     * Sets the event which will be executed before sending the error
     *
     * @param beforeSendEvent Custom event which will be executed before sending the error
     */
    public void setBeforeSendEvent(BeforeSendEvent beforeSendEvent) {
        config.setBeforeSendEvent(beforeSendEvent);
    }

    /**
     * Sends a report to Backtrace Console
     *
     * @param report Error report which will be sent
     */
    public void send(BacktraceReport report) {
        this.send(report, null);
    }

    /**
     * Sends a report to Backtrace Console and executes callback when receives a response
     *
     * @param report   Error report which will be sent
     * @param callback Event which will be executed after receiving a response
     */
    public void send(BacktraceReport report, OnServerResponseEvent callback) {
        this.backtrace.send(report, this.customAttributes, callback, config.isGatherAllThreads());
    }

    /**
     * Sends a message to Backtrace Console
     *
     * @param message Text message
     */
    public void send(String message) {
        this.send(message, null);
    }

    /**
     * Sends a message to Backtrace Console
     *
     * @param message  Text message
     * @param callback Event which will be executed after receiving a response
     */
    public void send(String message, OnServerResponseEvent callback) {
        this.send(new BacktraceReport(message), callback);
    }

    /**
     * Send an exception to Backtrace Console
     *
     * @param exception current exception
     */
    public void send(Exception exception) {
        this.send(exception, null);
    }

    /**
     * Sends an exception to Backtrace Console
     *
     * @param exception Current exception
     * @param callback  Event which will be executed after receiving a response
     */
    public void send(Exception exception, OnServerResponseEvent callback) {
        this.send(new BacktraceReport(exception), callback);
    }

    /**
     * Stop Backtrace Thread and wait until current processing message will be sent
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public void close() throws InterruptedException {
        this.close(this.config.isAwaitMessagesOnClose());
    }

    /**
     * Stop Backtrace Thread and wait until current processing message will be sent
     *
     * @param await if true wait until all added messages will be sent
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public void close(boolean await) throws InterruptedException {
        LOGGER.debug("Closing Backtrace Client - awaiting: " + await);
        if (await) {
            this.await();
        }
        this.backtrace.close();
    }

    /**
     * Wait until all messages in queue will be sent
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public void await() throws InterruptedException {
        this.backtrace.await();
    }

    /**
     * Wait until all messages in queue will be sent
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the {@code timeout} argument
     * @return {@code true} if all messages are sent in passed time and {@code false}
     * if the waiting time elapsed before all messages has been sent
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return this.backtrace.await(timeout, unit);
    }

    /**
     * Enable handling of uncaught exceptions, exceptions will be sent to the Backtrace Console
     */
    @SuppressWarnings("unused")
    public void enableUncaughtExceptionsHandler() {
        this.enableUncaughtExceptionsHandler(false);
    }

    /**
     * Enable handling of uncaught exceptions, exceptions will be sent to the Backtrace console
     *
     * @param blockThread wait until the error is sent
     */
    @SuppressWarnings("WeakerAccess")
    public void enableUncaughtExceptionsHandler(boolean blockThread) {
        BacktraceExceptionHandler.enable(this, blockThread);
    }

    /**
     * Disable using BacktraceExceptionHandler and sets default uncaught exception handler
     */
    @SuppressWarnings("unused")
    public void disableUncaughtExceptionsHandler() {
        BacktraceExceptionHandler.disable();
    }
}
