package backtrace.io.database;

import backtrace.io.BacktraceConfig;
import backtrace.io.BacktraceMessage;
import backtrace.io.data.BacktraceData;
import backtrace.io.data.BacktraceReport;
import backtrace.io.helpers.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BacktraceDatabase {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(backtrace.io.database.BacktraceDatabase.class);
    private final BacktraceDatabaseConfig config;


    private BacktraceDatabase(BacktraceDatabaseConfig config) {
        this.config = config;

        if (config.isDatabaseEnabled() && !createDatabaseDir()) {
            throw new IllegalArgumentException("Database path doesn't exist and can not be created");
        }
    }

    private boolean createDatabaseDir() {
        File dir = new File(this.config.getDatabasePath());
        return dir.exists() || dir.mkdir();
    }

    public static backtrace.io.database.BacktraceDatabase init(BacktraceConfig config, Queue<BacktraceMessage> queue) {
        if (config == null) {
            throw new NullPointerException("DatabaseConfig is null");
        }

        if (queue == null) {
            throw new NullPointerException("Passed queue is null");
        }

        backtrace.io.database.BacktraceDatabase database = new backtrace.io.database.BacktraceDatabase(config.getDatabaseConfig());
        if (config.getDatabaseConfig().isDatabaseEnabled()) {
            database.loadReports(queue);
        }
        return database;
    }

    private String getDatabaseDir() {
        File currentDirFile = new File(config.getDatabasePath());
        return currentDirFile.getAbsolutePath();
    }

    private String getFilePath(BacktraceReport backtraceReport) {
        return Paths.get(getDatabaseDir(), getFileName(backtraceReport)).toString();
    }

    private String getFileName(BacktraceReport report) {
        return report.getTimestamp() + "-" + report.getUuid() + "." + config.getFileExtension();
    }


    public void saveReport(BacktraceData backtraceData) {
        if (!config.isDatabaseEnabled()) {
            return;
        }

        boolean enoughSpace = this.deleteExcessDatabaseRecords();

        if (!enoughSpace) {
            LOGGER.warn("Not enough space to save report");
            return;
        }

        String filePath = getFilePath(backtraceData.getReport());
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(backtraceData);
        } catch (Exception e) {
            LOGGER.error("Can not save database report!", e);
        }
    }

    private boolean deleteExcessDatabaseRecords() {
        List<File> files = getDatabaseFiles();
        files.sort(FileHelper.getFileNameComparator());

        while ((config.isDatabaseNumberOfRecordsLimited() && this.getTotalNumberOfRecords() >= config.getDatabaseMaxRecordCount()) ||
                (config.isDatabaseSizeLimited() && this.getDatabaseSize() >= config.getDatabaseMaxSize())) {
            if (files.size() == 0) {
                LOGGER.warn("Database is empty, can not remove more files from database");
                break;
            }
            File fileToRemove = files.get(0);
            this.removeDatabaseFile(fileToRemove);
            files.remove(0);
        }

        // enough space
        return (!config.isDatabaseNumberOfRecordsLimited() && !config.isDatabaseSizeLimited()) ||
                (config.isDatabaseNumberOfRecordsLimited() && this.getTotalNumberOfRecords() < config.getDatabaseMaxRecordCount()) ||
                (config.isDatabaseSizeLimited() && this.getDatabaseSize() < config.getDatabaseMaxSize());
    }

    public void removeReport(BacktraceData backtraceData) {
        String filePath = getFilePath(backtraceData.getReport());
        File file = new File(filePath);
        removeDatabaseFile(file);
    }

    private List<File> getDatabaseFiles() {
        File databaseDir = new File(getDatabaseDir());
        File[] files = databaseDir.listFiles();

        if (files == null) {
            return new ArrayList<>();
        }

        String fileExtension = config.getFileExtension();
        List<File> databaseFiles = new ArrayList<>();

        for (final File f : files) {
            String extension = FileHelper.getFileExtension(f);

            if (!fileExtension.equals(extension)) {
                LOGGER.warn(String.format("File extension (%s) in database directory does not match to database files extension (%s)", fileExtension, extension));
                continue;
            }

            databaseFiles.add(f);
        }
        return databaseFiles;
    }

    public int getTotalNumberOfRecords() {
        return getDatabaseFiles().size();
    }

    public long getDatabaseSize() {
        long size = 0;
        List<File> files = getDatabaseFiles();
        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    private void loadReports(final Queue<BacktraceMessage> queue) {
        List<File> files = getDatabaseFiles();
        for (final File f : files) {
            BacktraceData report = loadReport(f);

            if (report == null) {
                LOGGER.warn("Loaded report from file is null");
                continue;
            }

            queue.add(new BacktraceMessage(report, null));
        }
    }

    private void removeDatabaseFile(File file) {
        if (file == null) {
            LOGGER.warn("File is null, can not be deleted");
            return;
        }
        if (!file.exists() || file.isDirectory()) {
            LOGGER.warn(String.format("File %s is directory or does not exist", file.getPath()));
            return;
        }

        LOGGER.info(String.format("Removing file %s from database", file.getPath()));

        if (!file.delete()) {
            LOGGER.warn(String.format("File %s can not be deleted", file.getPath()));
        }
    }

    private BacktraceData loadReport(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            ObjectInputStream reader = new ObjectInputStream(fileInputStream);
            return (BacktraceData) reader.readObject();
        } catch (InvalidClassException ice) {
            LOGGER.error("Can not load report from invalid file", ice);
            removeDatabaseFile(file);
        } catch (Exception e) {
            LOGGER.error("Can not load report from file", e);
        }
        return null;
    }
}
