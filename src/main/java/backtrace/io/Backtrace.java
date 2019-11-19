package backtrace.io;

import backtrace.io.events.OnServerResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Backtrace {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(Backtrace.class);
    private ConcurrentLinkedQueue<BacktraceMessage> queue;
    private BacktraceDatabase database;
    private final BacktraceConfig config;


    public Backtrace(BacktraceConfig config, ConcurrentLinkedQueue<BacktraceMessage> queue)
    {
        this.database = BacktraceDatabase.init(config, queue);
        this.config = config;
        this.queue = queue;
    }

    void handleBacktraceMessages(){
        while(true){
            try {
                BacktraceMessage message = queue.poll();

                if (message == null) {
                    continue;
                }

                processSingleBacktraceMessage(message);
            }
            catch (Exception e){
                LOGGER.error("Exception during pipeline for message from queue..", e);
            }
        }
    }

    private void processSingleBacktraceMessage(BacktraceMessage backtraceMessage){
        BacktraceData backtraceData = backtraceMessage.getBacktraceData();

        if (backtraceData == null) {
            LOGGER.warn("BacktraceData in queue is null");
            return;
        }

        this.database.saveReport(backtraceData);

        BacktraceResult result = ApiSender.sendReport(config.getServerUrl(), backtraceData);

        if (result.getStatus() == BacktraceResultStatus.Ok) {
            backtraceData.getReport().markAsSent();
            database.removeReport(backtraceData);
        } else {
            this.queue.add(backtraceMessage);
        }

        OnServerResponseEvent callback = backtraceMessage.getCallback();
        if (callback != null) {
            callback.onEvent(result);
        }
    }
}
