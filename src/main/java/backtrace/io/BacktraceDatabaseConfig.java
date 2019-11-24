package backtrace.io;

public class BacktraceDatabaseConfig {
    private String FILE_EXTENSION = "backtrace_report";
    private String DATABASE_PATH = ".backtrace.io/";
    private boolean USE_DATABASE = true;
    private int RETRY_LIMIT = 3;

    /**
     * @return
     */
    String getFileExtension() {
        return FILE_EXTENSION;
    }

    /**
     * @return
     */
    String getDatabasePath() {
        return DATABASE_PATH;
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

    public boolean useDatabase() {
        return USE_DATABASE;
    }

    /**
     *
     * @param databasePath
     */
    void setDatabasePath(String databasePath){
        this.DATABASE_PATH = databasePath;
    }

    void setDatabaseRetryLimit(int retryLimit){
        if(retryLimit < 0 ){
            throw new IllegalArgumentException("Retry limit value should be greater than or equal to zero");
        }
        this.RETRY_LIMIT = retryLimit;
    }
}
