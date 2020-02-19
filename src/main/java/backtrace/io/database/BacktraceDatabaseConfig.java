package backtrace.io.database;

public class BacktraceDatabaseConfig {
    @SuppressWarnings("FieldCanBeLocal")
    private volatile String FILE_EXTENSION = "backtrace_report";
    private volatile String DATABASE_PATH = ".backtrace.io";
    private volatile boolean USE_DATABASE = true;
    private volatile int RETRY_LIMIT = 3;
    private volatile int MAX_RECORD_COUNT = -1;
    private volatile long MAX_DATABASE_SIZE = -1; // -1 is unlimited

    public int getDatabaseMaxRecordCount() {
        return MAX_RECORD_COUNT;
    }

    public long getDatabaseMaxSize() {
        return MAX_DATABASE_SIZE;
    }

    public String getFileExtension() {
        return FILE_EXTENSION;
    }

    public String getDatabasePath() {
        return DATABASE_PATH;
    }

    public int getDatabaseRetryLimit() {
        return RETRY_LIMIT;
    }

    boolean isDatabaseNumberOfRecordsLimited() {
        return MAX_RECORD_COUNT != -1;
    }

    boolean isDatabaseSizeLimited() {
        return MAX_DATABASE_SIZE != -1;
    }

    public boolean isDatabaseEnabled() {
        return USE_DATABASE;
    }

    public void disableDatabase() {
        this.USE_DATABASE = false;
    }

    public void enableDatabase() {
        this.USE_DATABASE = true;
    }

    public void setDatabasePath(String databasePath) {
        this.DATABASE_PATH = databasePath;
    }

    public void setDatabaseRetryLimit(int retryLimit) {
        if (retryLimit < 0) {
            throw new IllegalArgumentException("Retry limit value should be greater than or equal to zero");
        }
        this.RETRY_LIMIT = retryLimit;
    }

    public void setMaxDatabaseSize(long maxDatabaseSize) {
        this.MAX_DATABASE_SIZE = maxDatabaseSize;
    }

    public void setMaxRecordCount(int maxRecordCount) {
        this.MAX_RECORD_COUNT = maxRecordCount;
    }
}
