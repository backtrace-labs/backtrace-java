package backtrace.io;

public class BacktraceDatabaseConfig {
    private String FILE_EXTENSION = "backtrace_report";
    private String DATABASE_PATH = "./backtrace.io/";
    private boolean SAVE_TO_DATABASE = true;

    /**
     *
     * @return
     */
    String getFileExtension() {
        return FILE_EXTENSION;
    }

    /**
     *
     * @return
     */
    String getDatabasePath() {
        return DATABASE_PATH;
    }

    /**
     *
     * @return
     */
    public boolean isSaveToDatabase() {
        return SAVE_TO_DATABASE;
    }

}
