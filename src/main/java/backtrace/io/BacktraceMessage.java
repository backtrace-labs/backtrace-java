package backtrace.io;

import backtrace.io.events.OnServerResponseEvent;

/**
 * The message that is sent from the application, which will be added to the queue and handled by the thread
 * which sending the message. After receiving the response, callback event will be executed.
 */
class BacktraceMessage {
    private BacktraceData backtraceData;
    private OnServerResponseEvent callback;

    /**
     * Creates new instance of BacktraceMessage
     * @param backtraceData Message which contains information about error, attributes and threads
     * @param callback Event which will be executed after receiving the response
     */
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
