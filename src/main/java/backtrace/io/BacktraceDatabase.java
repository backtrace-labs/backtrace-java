package backtrace.io;

import com.google.common.io.Files;

import java.io.*;
import java.util.Queue;

public class BacktraceDatabase {
    private static final String FILE_EXTENSION = "backtrace_report";
    private static final String DATABASE_PATH = "./backtrace";
    private static boolean SAVE_TO_DATABASE = true;

    public BacktraceDatabase() {

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


    public void saveReport(BacktraceReport report){
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

    public void removeReport(BacktraceReport report){
        File file = new File(getFilePath(report));

        if(!file.exists() || file.isDirectory()) {
            // TODO: log that
            return;
        }

        if(!file.delete()){
            // TODO: log that
        }
    }

    protected void loadReports(final Queue<BacktraceReport> queue){
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
}
