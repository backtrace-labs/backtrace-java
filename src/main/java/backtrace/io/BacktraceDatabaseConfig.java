package backtrace.io;

public class BacktraceDatabaseConfig {
    private String FILE_EXTENSION = "backtrace_report";
    private String DATABASE_PATH = ".backtrace.io/";
    private boolean USE_DATABASE = true;
    private int RETRY_LIMIT = 3;
    private int MAX_RECORD_COUNT = -1;
    private long MAX_DATABASE_SIZE = -1; // unlimited


    public int getMaxRecordCount() {
        return MAX_RECORD_COUNT;
    }

    public long getMaxDatabaseSize() {
        return MAX_DATABASE_SIZE;
    }

    /**
     * @return
     */
    String getFileExtension() {
        return FILE_EXTENSION;
    }

    /**
     * @return
     */
    public String getDatabasePath() {
        return DATABASE_PATH;
    }

    boolean isNumberOfRecordsLimited(){
        return MAX_RECORD_COUNT != -1;
    }

    boolean isDatabaseSizeLimited(){
        return MAX_DATABASE_SIZE != -1;
    }


    public int getRetryLimit() {
        return RETRY_LIMIT;
    }

    /**
     * @return
     */

    public void disableDatabase() {
        this.USE_DATABASE = false;
    }

    boolean useDatabase() {
        return USE_DATABASE;
    }

    /**
     *
     * @param databasePath
     */
    public void setDatabasePath(String databasePath){
        this.DATABASE_PATH = databasePath;
    }

    public void setDatabaseRetryLimit(int retryLimit){
        if(retryLimit < 0){
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
