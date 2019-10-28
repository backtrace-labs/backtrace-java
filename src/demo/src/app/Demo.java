package app;

import backtrace.io.BacktraceClient;
import backtrace.io.BacktraceReport;

public class Demo {
    public static void main(String[] args) {
        BacktraceReport report = new BacktraceReport("test message");
        BacktraceClient client = new BacktraceClient();
        client.send(report);
        try {
            System.out.println("[Main Thread] waiting..");
            report.waitUntilSent();
            System.out.println("[Main Thread] Sleeping..");
            Thread.sleep(20000);
        }
        catch (Exception e){
            System.out.println(e);
        }
        System.out.println("[Main Thread] end.");
    }
}