package backtrace.io;

import java.util.concurrent.ConcurrentLinkedQueue;

class Backtrace {
    private ConcurrentLinkedQueue<BacktraceData> queue;

    Backtrace(BacktraceConfig config){
        queue = new ConcurrentLinkedQueue<>();
        BacktraceThread.init(config, queue);
    }

    void send(BacktraceReport report){
        BacktraceData backtraceData = new BacktraceData(report, null);
        queue.add(backtraceData);
    }
}
