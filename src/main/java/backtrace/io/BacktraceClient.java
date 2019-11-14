package backtrace.io;


import backtrace.io.events.OnServerResponseEventListener;

public class BacktraceClient {
    private Backtrace backtrace;

    public BacktraceClient(BacktraceConfig config){
        backtrace = new Backtrace(config);
    }

    public void send(BacktraceReport report){
        this.send(report, null);
    }

    public void send(BacktraceReport report, OnServerResponseEventListener callback){
        this.backtrace.send(report, callback);
    }
}
