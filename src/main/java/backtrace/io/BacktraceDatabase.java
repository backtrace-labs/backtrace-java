package backtrace.io;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class BacktraceDatabase {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(BacktraceDatabase.class);
    private final BacktraceDatabaseConfig config;


    private BacktraceDatabase(BacktraceDatabaseConfig config) {
        this.config = config;
    }

    static BacktraceDatabase init(BacktraceConfig config, ConcurrentLinkedQueue<BacktraceMessage> queue) {
        BacktraceDatabase database = new BacktraceDatabase(config.getDatabaseConfig());
        database.loadReports(queue);
        return database;
    }

    private String getDatabaseDir() {
        File currentDirFile = new File(config.getDatabasePath());
        return currentDirFile.getAbsolutePath();
    }

    private String getFilePath(BacktraceReport backtraceReport) {
        return getDatabaseDir() + "\\" + getFileName(backtraceReport);
    }

    private String getFileName(BacktraceReport report) {
        return report.getTimestamp() + "-" + report.getUuid() + "." + config.getFileExtension();
    }


    void saveReport(BacktraceData backtraceData) {
        String filePath = getFilePath(backtraceData.getReport());
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(backtraceData);
        } catch (Exception e) {
            LOGGER.error("Can not save database report!", e);
        }
    }

    void removeReport(BacktraceData backtraceData) {
        String filePath = getFilePath(backtraceData.getReport());
        File file = new File(filePath);

        if (!file.exists() || file.isDirectory()) {
            LOGGER.warn(String.format("File %s is directory or does not exist", filePath));
            return;
        }

        if (!file.delete()) {
            LOGGER.warn(String.format("File %s can not be deleted", filePath));
        }
    }

    private void loadReports(final Queue<BacktraceMessage> queue) {
        File databaseDir = new File(getDatabaseDir());
        File[] files = databaseDir.listFiles();
        String fileExtension = config.getFileExtension();

        if (files == null) {
            return;
        }

        for (final File f : files) {
            String extension = Files.getFileExtension(f.getAbsolutePath());

            if (!fileExtension.equals(extension)) {
                LOGGER.warn(String.format("File extension (%s) in database directory does not match to database files extension (%s)", fileExtension, extension));
                continue;
            }

            BacktraceData report = loadReport(f);

            if (report == null) {
                LOGGER.warn("Current report is null");
                continue;
            }

            queue.add(new BacktraceMessage(report, null));
        }
    }

    private BacktraceData loadReport(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            ObjectInputStream reader = new ObjectInputStream(fileInputStream);
            return (BacktraceData) reader.readObject();
        } catch (Exception e) {
            LOGGER.error("Can not load report from file", e);
        }
        return null;
    }
}
