package backtrace.io;


public class BacktraceClient {
    private Backtrace backtrace;

    public BacktraceClient(){
        backtrace = new Backtrace();
    }

    public void send(String message) {
        backtrace.addElement(message);
    }

    public void send(BacktraceReport report ){
        backtrace.addElement(report);
    }

}
