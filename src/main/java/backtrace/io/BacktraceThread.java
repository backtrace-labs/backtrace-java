package backtrace.io;

import com.google.common.io.Files;

import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BacktraceThread extends Thread {

     private ConcurrentLinkedQueue<BacktraceReport> queue;
     private static final String FILE_EXTENSION = "backtrace_report";
     private static final String DATABASE_PATH = "./backtrace";

    public BacktraceThread(ConcurrentLinkedQueue<BacktraceReport> queue){
        super();
        this.queue = queue;
        loadUnsentReports();
    }

    @Override
    public void run(){
        while(true){
            BacktraceReport report = queue.poll();
            if (report == null){
                continue;
            }
            try {
                System.out.println("[BacktraceThread] " + report.message);
                System.out.println("[BacktraceThread] Sleeping..");
                pipeline(report);
//                Thread.sleep(10000);
                System.out.println("[BacktraceThread] 10000..");
            }
            catch (Exception e){
                System.out.println("[BacktraceThread] Exception");
                System.out.println(e);
            }
//            System.out.println("[BacktraceThread] Before sent");
//            sendHttp(report);
//            System.out.println("[BacktraceThread] After sent");
        }
    }

    private void pipeline(BacktraceReport report){
        saveReport(report);
        sendHttp(report);
        removeReport(report);
    }

    private String getDatabaseDir(){
        File currentDirFile = new File(".backtrace");
        return currentDirFile.getAbsolutePath();
    }

    private String getFilePath(BacktraceReport report){
        return getDatabaseDir() + "\\" + getFileName(report);
    }

    private String getFileName(BacktraceReport report){
        return report.getTimestamp() + "." + FILE_EXTENSION;
    }

    private void saveReport(BacktraceReport report){
        String filePath = getFilePath(report);
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(report);
        }
        catch (Exception e){
            System.out.println(e);
                //TODO : log that
        }
    }

    private void removeReport(BacktraceReport report){
        File file = new File(getFilePath(report));

        if(!file.exists() || file.isDirectory()) {
            // TODO: log that
            return;
        }

        if(!file.delete()){
            // TODO: log that
        }
    }

    private void loadUnsentReports(){
        File databaseDir = new File(getDatabaseDir());
        File[] files = databaseDir.listFiles();
        for (final File f : files) {
            String extension = Files.getFileExtension(f.getAbsolutePath());

            if(!FILE_EXTENSION.equals(extension)){
                continue;
            }

            BacktraceReport report = loadReport(f);

            if (report == null){
                continue;
            }

            queue.add(report);
//            report.setAsSent();
            System.out.println(report.message);
        }
    }

    private BacktraceReport loadReport(File file){
        try(FileInputStream fileInputStream = new FileInputStream(file)){
            ObjectInputStream reader = new ObjectInputStream(fileInputStream);
            BacktraceReport report = (BacktraceReport)reader.readObject();
            return report;
        }
        catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    private void sendHttp(BacktraceReport report){
        System.out.println(report.message);
        report.setAsSent();
        System.out.println("");
    }
}