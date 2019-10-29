package backtrace.io;


import java.util.concurrent.ConcurrentLinkedQueue;

public class BacktraceThread extends Thread {

    private ConcurrentLinkedQueue<BacktraceReport> queue;
    private BacktraceDatabase database;

    public BacktraceThread(ConcurrentLinkedQueue<BacktraceReport> queue){
        super();
        this.queue = queue;
        database = new BacktraceDatabase();
        database.loadReports(this.queue);
    }

    @Override
    public void run(){
        while(true){
            BacktraceReport report = queue.poll();
            if (report == null){
                continue;
            }
            try {
                System.out.println("[BacktraceThread] " + report.message);
                System.out.println("[BacktraceThread] Sleeping..");
                pipeline(report);
//                Thread.sleep(10000);
                System.out.println("[BacktraceThread] 10000..");
            }
            catch (Exception e){
                System.out.println("[BacktraceThread] Exception");
                System.out.println(e);
            }
        }
    }

    private void pipeline(BacktraceReport report){
        database.saveReport(report);
        sendHttp(report);
        database.removeReport(report);
    }
    private void sendHttp(BacktraceReport report){
        System.out.println(report.message);
        report.setAsSent();
        System.out.println("");
    }
}