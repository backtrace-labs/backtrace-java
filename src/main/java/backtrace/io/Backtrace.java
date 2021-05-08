package backtrace.io;

import backtrace.io.data.BacktraceData;
import backtrace.io.data.BacktraceReport;
import backtrace.io.database.BacktraceDatabase;
import backtrace.io.events.OnServerResponseEvent;
import backtrace.io.http.ApiSender;
import backtrace.io.http.BacktraceResult;
import backtrace.io.http.BacktraceResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Backtrace {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(Backtrace.class);
    private BacktraceQueue queue;
    private BacktraceDatabase database;
    private final BacktraceConfig config;

    /**
     * Creates Backtrace instance
     *
     * @param config Library configuration
     * @param queue  Queue containing error reports that should be sent to the Backtrace console
     */
    Backtrace(BacktraceConfig config, BacktraceQueue queue) {
        this.database = BacktraceDatabase.init(config, queue);
        this.config = config;
        this.queue = queue;
    }

    /**
     * Handles the queue of incoming error reports
     */
    void handleBacktraceMessage() {
        if (queue.isEmpty()) {
            this.queue.unlock();
            this.queue.queueIsEmpty();
        }

        try {
            this.queue.awaitNewMessage();
            BacktraceMessage message = queue.poll();
            if (message != null) {
                processSingleBacktraceMessage(message);
            }
        } catch (Exception e) {
            LOGGER.error("Exception during pipeline for message from queue..", e);
        }
    }

    /**
     * Process a single message from the queue
     *
     * @param backtraceMessage message containing error report and callback
     */
    private void processSingleBacktraceMessage(BacktraceMessage backtraceMessage) {
        BacktraceData backtraceData = backtraceMessage.getBacktraceData();

        if (backtraceData == null) {
            LOGGER.warn("BacktraceData in queue is null");
            return;
        }

        this.database.saveReport(backtraceData);
        LOGGER.debug("Message from current report: " + backtraceData.getReport().getMessage());
        if (config.getBeforeSendEvent() != null) {
            LOGGER.debug("Custom before sending event");
            backtraceData = config.getBeforeSendEvent().onEvent(backtraceData);
        }

        BacktraceResult result = this.sendReport(backtraceData);

        this.handleResponse(result, backtraceMessage);

        OnServerResponseEvent callback = backtraceMessage.getCallback();
        if (callback != null) {
            LOGGER.debug("Custom callback");
            callback.onEvent(result);
        }
    }

    /**
     * Sends a error report using custom request handler or send it to the Backtrace console by a default method
     *
     * @param backtraceData error report
     * @return server response
     */
    private BacktraceResult sendReport(BacktraceData backtraceData) {
        if (this.config.getRequestHandler() != null) {
            LOGGER.debug("Custom request handler");
            return this.config.getRequestHandler().onRequest(backtraceData);
        }
        LOGGER.debug("Default request handler");
        return ApiSender.sendReport(config.getSubmissionUrl(), backtraceData);
    }

    /**
     * Depending on the status of the response from the server, it performs various processing flows.
     * If successful, it marks the report as sent and deletes it from the database.
     * In case of failure, if the repetition limit is not exceeded, it adds to the queue.
     *
     * @param result           server response
     * @param backtraceMessage message containing error report and callback
     */
    private void handleResponse(BacktraceResult result, BacktraceMessage backtraceMessage) {
        if (result.getStatus() == BacktraceResultStatus.Ok) {
            if (config.getDatabaseConfig().isDatabaseEnabled()) {
                database.removeReport(backtraceMessage.getBacktraceData());
            }
            return;
        }

        BacktraceReport report = backtraceMessage.getBacktraceData().getReport();
        if (result.shouldRetry() && report.getRetryCounter() < config.getDatabaseConfig().getDatabaseRetryLimit()) {
            report.incrementRetryCounter();
            this.queue.addWithLock(backtraceMessage);
        }
    }

    void close() {
        this.queue.close();
    }
}
