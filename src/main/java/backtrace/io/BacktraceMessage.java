package backtrace.io;

import backtrace.io.events.OnServerResponseEvent;

class BacktraceMessage {
    private BacktraceData backtraceData;
    private OnServerResponseEvent callback;

    BacktraceMessage(BacktraceData backtraceData, OnServerResponseEvent callback) {
        this.backtraceData = backtraceData;
        this.callback = callback;
    }

    BacktraceData getBacktraceData() {
        return backtraceData;
    }

    OnServerResponseEvent getCallback() {
        return callback;
    }
}
