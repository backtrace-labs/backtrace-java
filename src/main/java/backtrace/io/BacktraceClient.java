package backtrace.io;


public class BacktraceClient {
    private Backtrace backtrace;

    public BacktraceClient(BacktraceConfig config){
        backtrace = new Backtrace(config);
    }

    public void send(BacktraceReport report){
        backtrace.send(report);
    }
}
