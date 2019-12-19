package app;

import backtrace.io.BacktraceClient;
import backtrace.io.BacktraceConfig;
import backtrace.io.data.BacktraceReport;

public class Demo {
    public static void main(String[] args) {
        BacktraceReport report = new BacktraceReport("test message");
        BacktraceClient client = new BacktraceClient(new BacktraceConfig("<endpoint url>", "<submission token>"));
        client.send(report);
        try {
            report.await(); // wait for sending report
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}