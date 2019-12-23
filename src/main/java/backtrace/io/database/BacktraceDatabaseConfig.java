package backtrace.io.database;

public class BacktraceDatabaseConfig {
    @SuppressWarnings("FieldCanBeLocal")
    private String FILE_EXTENSION = "backtrace_report";
    private String DATABASE_PATH = ".backtrace.io";
    private boolean USE_DATABASE = true;
    private int RETRY_LIMIT = 3;
    private int MAX_RECORD_COUNT = -1;
    private long MAX_DATABASE_SIZE = -1; // -1 is unlimited

    int getDatabaseMaxRecordCount() {
        return MAX_RECORD_COUNT;
    }

    long getDatabaseMaxSize() {
        return MAX_DATABASE_SIZE;
    }

    String getFileExtension() {
        return FILE_EXTENSION;
    }

    String getDatabasePath() {
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
