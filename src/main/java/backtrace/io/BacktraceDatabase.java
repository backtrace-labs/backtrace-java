package backtrace.io;

import com.google.common.io.Files;

import java.io.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class BacktraceDatabase {

    private final BacktraceDatabaseConfig config;


    private BacktraceDatabase(BacktraceDatabaseConfig config) {
        this.config = config;
    }

    static BacktraceDatabase init(BacktraceConfig config, ConcurrentLinkedQueue<BacktraceMessage> queue){
        BacktraceDatabase database = new BacktraceDatabase(config.getDatabaseConfig());
        database.loadReports(queue);
        return database;
    }

    private String getDatabaseDir(){
        File currentDirFile = new File(config.getDatabasePath());
        return currentDirFile.getAbsolutePath();
    }

    private String getFilePath(BacktraceReport backtraceReport){
        return getDatabaseDir() + "\\" + getFileName(backtraceReport);
    }

    private String getFileName(BacktraceReport report){
        return report.getTimestamp() + "-" + report.getUuid() + "." + config.getFileExtension();
    }


    void saveReport(BacktraceData backtraceData){
        String filePath = getFilePath(backtraceData.report);
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(backtraceData);
        }
        catch (Exception e){
            System.out.println(e);
            //TODO : log that
        }
    }

    void removeReport(BacktraceData backtraceData){
        File file = new File(getFilePath(backtraceData.report));

        if(!file.exists() || file.isDirectory()) {
            // TODO: log that
            return;
        }

        if(!file.delete()){
            // TODO: log that
        }
    }

    private void loadReports(final Queue<BacktraceMessage> queue){
        File databaseDir = new File(getDatabaseDir());
        File[] files = databaseDir.listFiles();
        String fileExtension = config.getFileExtension();
        for (final File f : files) {
            String extension = Files.getFileExtension(f.getAbsolutePath());

            if(!fileExtension.equals(extension)){
                continue;
            }

            BacktraceData report = loadReport(f);

            if (report == null){
                continue;
            }

            queue.add(new BacktraceMessage(report, null));
        }
    }

    private BacktraceData loadReport(File file){
        try(FileInputStream fileInputStream = new FileInputStream(file)){
            ObjectInputStream reader = new ObjectInputStream(fileInputStream);
            return (BacktraceData)reader.readObject();
        }
        catch (Exception e){
            System.out.println(e);
        }
        return null;
    }
}
