package app;

import backtrace.io.BacktraceClient;
import backtrace.io.BacktraceConfig;
import backtrace.io.data.BacktraceReport;

public class Demo {
    public static void main(String[] args) throws InterruptedException {
        BacktraceReport report = new BacktraceReport("test message");
        BacktraceClient client = new BacktraceClient(new BacktraceConfig("<endpoint url>", "<submission token>"));
        client.send(report);
        client.await();
    }
}