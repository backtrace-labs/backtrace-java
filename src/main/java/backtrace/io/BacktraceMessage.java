package backtrace.io;

import backtrace.io.BacktraceData;
import backtrace.io.events.OnServerResponseEventListener;

public class BacktraceMessage {
    BacktraceData backtraceData;
    OnServerResponseEventListener callback;

    BacktraceMessage(BacktraceData backtraceData, OnServerResponseEventListener callback){
        this.backtraceData = backtraceData;
        this.callback = callback;
    }

    public BacktraceData getBacktraceData() {
        return backtraceData;
    }

    public OnServerResponseEventListener getCallback() {
        return callback;
    }
}
