package backtrace.io;


import java.util.concurrent.ConcurrentLinkedQueue;

public class BacktraceThread extends Thread {
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
//        return thread;
    }

    @Override
    public void run(){
        while(true){
            BacktraceMessage message = queue.poll();

            if (message == null) {
                continue;
            }
            
            BacktraceData backtraceData = message.getBacktraceData();

            if (backtraceData == null){
                continue;
            }
            try {
//                System.out.println("[BacktraceThread] " + backtraceData.report.message);
//                System.out.println("[BacktraceThread] Single pipeline..");
                pipeline(backtraceData);
//                System.out.println("[BacktraceThread] Finished");
            }
            catch (Exception e){
                System.out.println("[BacktraceThread] Exception");
                System.out.println(e);
            }
        }
    }

    private void pipeline(BacktraceData backtraceData){
        database.saveReport(backtraceData);
        String json = BacktraceSerializeHelper.toJson(backtraceData);

        BacktraceReportSender.sendReport(config.getServerUrl(), json, null, backtraceData.report, null); // TODO:

        backtraceData.report.setAsSent();

//        database.removeReport(backtraceData); //TODO: remove only when success
    }
}