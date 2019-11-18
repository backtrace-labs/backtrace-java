package backtrace.io;

import backtrace.io.events.OnServerResponseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

class Backtrace {
    private ConcurrentLinkedQueue<BacktraceMessage> queue;

    Backtrace(BacktraceConfig config){
        queue = new ConcurrentLinkedQueue<>();
        BacktraceThread.init(config, queue);
    }

    void send(BacktraceReport report, Map<String, Object> attributes, OnServerResponseEvent callback){
        BacktraceData backtraceData = new BacktraceData(report, attributes);
        queue.add(new BacktraceMessage(backtraceData, callback));
    }
}
