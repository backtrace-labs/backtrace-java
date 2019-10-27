package backtrace.io;

import java.util.concurrent.*;

class Backtrace {
    private ConcurrentLinkedQueue<BacktraceReport> queue;
    private BacktraceThread thread;

    Backtrace(){

        queue = new ConcurrentLinkedQueue<>();
        this.queue = queue;

        thread = new BacktraceThread(queue);
        thread.setDaemon(true);
        thread.start();
    }

    void addElement(String message) {
        BacktraceReport report = new BacktraceReport(message);
        System.out.println("[Main Thread] before adding to queue");
        this.addElement(report);
        System.out.println("[Main Thread] after adding to queue");
    }

    void addElement(BacktraceReport report) {
        queue.add(report);
    }
}
