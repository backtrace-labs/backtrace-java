package backtrace.io;

import backtrace.io.events.OnServerResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

class Backtrace {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(Backtrace.class);
    private ConcurrentLinkedQueue<BacktraceMessage> queue;
    private BacktraceDatabase database;
    private final BacktraceConfig config;

    /**
     * Creates Backtrace instance
     * @param config Library configuration
     * @param queue Queue containing error reports that should be sent to the Backtrace console
     */
    public Backtrace(BacktraceConfig config, ConcurrentLinkedQueue<BacktraceMessage> queue) {
        this.database = BacktraceDatabase.init(config, queue);
        this.config = config;
        this.queue = queue;
    }

    /**
     * Handles the queue of incoming error reports
     */
    void handleBacktraceMessages() {
        while (true) {
            try {
                BacktraceMessage message = queue.poll();

                if (message == null) {
                    continue;
                }

                processSingleBacktraceMessage(message);
            } catch (Exception e) {
                LOGGER.error("Exception during pipeline for message from queue..", e);
            }
        }
    }

    /**
     * Process a single message from the queue
     * @param backtraceMessage message containing error report and callback
     */
    private void processSingleBacktraceMessage(BacktraceMessage backtraceMessage) {
        BacktraceData backtraceData = backtraceMessage.getBacktraceData();

        if (backtraceData == null) {
            LOGGER.warn("BacktraceData in queue is null");
            return;
        }

        if(config.getDatabaseConfig().isDatabaseEnabled()) {
            this.database.saveReport(backtraceData);
        }

        if(config.getBeforeSendEvent() != null){
            backtraceData = config.getBeforeSendEvent().onEvent(backtraceData);
        }

        BacktraceResult result = this.sendReport(backtraceData);

        this.handleResponse(result, backtraceMessage);

        OnServerResponseEvent callback = backtraceMessage.getCallback();
        if (callback != null) {
            callback.onEvent(result);
        }
    }

    /**
     * Sends a error report using custom request handler or send it to the Backtrace console by a default method
     * @param backtraceData error report
     * @return server response
     */
    private BacktraceResult sendReport(BacktraceData backtraceData){
        if(this.config.getRequestHandler() != null){
            return this.config.getRequestHandler().onRequest(backtraceData);
        }
        return ApiSender.sendReport(config.getSubmissionUrl(), backtraceData);
    }

    /**
     * Depending on the status of the response from the server, it performs various processing flows.
     * If successful, it marks the report as sent and deletes it from the database.
     * In case of failure, if the repetition limit is not exceeded, it adds to the queue.
     * @param result server response
     * @param backtraceMessage message containing error report and callback
     */
    private void handleResponse(BacktraceResult result, BacktraceMessage backtraceMessage){
        if (result.getStatus() == BacktraceResultStatus.Ok) {
            backtraceMessage.getBacktraceData().getReport().markAsSent();
            if(config.getDatabaseConfig().isDatabaseEnabled()) {
                database.removeReport(backtraceMessage.getBacktraceData());
            }
            return;
        }

        BacktraceReport report = backtraceMessage.getBacktraceData().getReport();
        if(report.getRetryCounter() < config.getDatabaseConfig().getDatabaseRetryLimit()){
            report.incrementRetryCounter();
            this.queue.add(backtraceMessage);
        }
    }
}
