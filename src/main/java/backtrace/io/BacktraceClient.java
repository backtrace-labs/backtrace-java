package backtrace.io;


import backtrace.io.events.OnServerResponseEventListener;

public class BacktraceClient {
    private Backtrace backtrace;

    public BacktraceClient(BacktraceConfig config){
        backtrace = new Backtrace(config);
    }


    public void enableUncaughtExceptionsHandler(){
        this.enableUncaughtExceptionsHandler(false);
    }

    public void enableUncaughtExceptionsHandler(boolean blockMainThread){
        BacktraceExceptionHandler.enable(this, blockMainThread);
    }

    public void send(BacktraceReport report){
        this.send(report, null);
    }

    public void send(BacktraceReport report, OnServerResponseEventListener callback){
        this.backtrace.send(report, callback);
    }
}
