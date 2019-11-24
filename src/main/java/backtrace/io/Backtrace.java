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


    public Backtrace(BacktraceConfig config, ConcurrentLinkedQueue<BacktraceMessage> queue) {
        this.database = BacktraceDatabase.init(config, queue);
        this.config = config;
        this.queue = queue;
    }

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

    private void processSingleBacktraceMessage(BacktraceMessage backtraceMessage) {
        BacktraceData backtraceData = backtraceMessage.getBacktraceData();

        if (backtraceData == null) {
            LOGGER.warn("BacktraceData in queue is null");
            return;
        }

        if(config.getDatabaseConfig().useDatabase()) {
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

    private BacktraceResult sendReport(BacktraceData backtraceData){
        if(this.config.getRequestHandler() != null){
            return this.config.getRequestHandler().onRequest(backtraceData);
        }
        return ApiSender.sendReport(config.getServerUrl(), backtraceData);
    }

    private void handleResponse(BacktraceResult result, BacktraceMessage backtraceMessage){
        System.out.println("test");
        if (result.getStatus() == BacktraceResultStatus.Ok) {
            backtraceMessage.getBacktraceData().getReport().markAsSent();
            if(config.getDatabaseConfig().useDatabase()) {
                database.removeReport(backtraceMessage.getBacktraceData());
            }
            return;
        }

        BacktraceReport report = backtraceMessage.getBacktraceData().getReport();
        if(report.incrementAndGetRetryCounter() < config.getDatabaseConfig().getRetryLimit()){
            this.queue.add(backtraceMessage);
        }
    }
}
