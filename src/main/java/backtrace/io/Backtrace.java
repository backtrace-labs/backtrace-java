package backtrace.io;

import backtrace.io.events.OnServerResponseEventListener;

import java.util.concurrent.ConcurrentLinkedQueue;

class Backtrace {
    private ConcurrentLinkedQueue<BacktraceMessage> queue;

    Backtrace(BacktraceConfig config){
        queue = new ConcurrentLinkedQueue<>();
        BacktraceThread.init(config, queue);
    }

    void send(BacktraceReport report, OnServerResponseEventListener callback){
        BacktraceData backtraceData = new BacktraceData(report, null);
        queue.add(new BacktraceMessage(backtraceData, callback));
    }
}
