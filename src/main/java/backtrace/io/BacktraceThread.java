package backtrace.io;


import java.util.concurrent.ConcurrentLinkedQueue;

public class BacktraceThread extends Thread {

    private ConcurrentLinkedQueue<BacktraceData> queue;
    private BacktraceDatabase database;
    private final BacktraceConfig config;


    public BacktraceThread(BacktraceConfig config, ConcurrentLinkedQueue<BacktraceData> queue){
        super();
        this.database = BacktraceDatabase.init(config, queue);
        this.config = config;
        this.queue = queue;
    }

    static BacktraceThread init(BacktraceConfig config, ConcurrentLinkedQueue<BacktraceData> queue){
        BacktraceThread thread = new BacktraceThread(config, queue);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    @Override
    public void run(){
        while(true){
            BacktraceData backtraceData = queue.poll();
            if (backtraceData == null){
                continue;
            }
            try {
                System.out.println("[BacktraceThread] " + backtraceData.report.message);
                System.out.println("[BacktraceThread] Single pipeline..");
                pipeline(backtraceData);
                System.out.println("[BacktraceThread] Finished");
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

        database.removeReport(backtraceData);
    }
}