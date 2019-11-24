package backtrace.io;

public class BacktraceDatabaseConfig {
    private String FILE_EXTENSION = "backtrace_report";
    private String DATABASE_PATH = ".backtrace.io/";
    private boolean SAVE_TO_DATABASE = true;
    private Integer RETRY_LIMIT = 3;

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


    public Integer getRetryLimit() {
        return RETRY_LIMIT;
    }

    /**
     * @return
     */
    public boolean isSaveToDatabase() {
        return SAVE_TO_DATABASE;
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
