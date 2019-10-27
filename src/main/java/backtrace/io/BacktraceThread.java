package backtrace.io;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BacktraceThread extends Thread {

    private ConcurrentLinkedQueue<BacktraceReport> queue;

    public BacktraceThread(ConcurrentLinkedQueue<BacktraceReport> queue){
        super();
        this.queue = queue;
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
                Thread.sleep(10000);
                System.out.println("[BacktraceThread] 10000..");
            }
            catch (Exception e){
                System.out.println("[BacktraceThread] Exception");
                System.out.println(e);
            }
            System.out.println("[BacktraceThread] Before sent");
            sendHttp(report);
            System.out.println("[BacktraceThread] After sent");
        }
    }

    private void sendHttp(BacktraceReport report){
        report.setAsSent();
        System.out.println("");
    }
}