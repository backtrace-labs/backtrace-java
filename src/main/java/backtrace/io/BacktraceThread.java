package backtrace.io;


import backtrace.io.events.OnServerResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BacktraceThread extends Thread {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(BacktraceThread.class);
    private final static String THREAD_NAME = "backtrace-deamon";

    private ConcurrentLinkedQueue<BacktraceMessage> queue;
    private BacktraceDatabase database;
    private final BacktraceConfig config;


    private BacktraceThread(BacktraceConfig config, ConcurrentLinkedQueue<BacktraceMessage> queue){
        super();
        this.database = BacktraceDatabase.init(config, queue);
        this.config = config;
        this.queue = queue;
    }

    static void init(BacktraceConfig config, ConcurrentLinkedQueue<BacktraceMessage> queue){
        BacktraceThread thread = new BacktraceThread(config, queue);
        thread.setDaemon(true);
        thread.setName(THREAD_NAME);
        thread.start();
    }

    @Override
    public void run(){
        while(true){
            BacktraceMessage message = queue.poll();

            if (message == null) {
                continue;
            }

            try {
                pipeline(message);
            }
            catch (Exception e){
                LOGGER.error("Exception during pipeline for message from queue..", e);
            }
        }
    }

    private void pipeline(BacktraceMessage backtraceMessage) {
        BacktraceData backtraceData = backtraceMessage.getBacktraceData();

        if (backtraceData == null) {
            LOGGER.warn("BacktraceData in queue is null");
            return;
        }

        database.saveReport(backtraceData);

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