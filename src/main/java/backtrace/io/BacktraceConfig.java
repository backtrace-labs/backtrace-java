package backtrace.io;

import backtrace.io.database.BacktraceDatabaseConfig;
import backtrace.io.events.BeforeSendEvent;
import backtrace.io.events.RequestHandler;

import java.net.URI;

public class BacktraceConfig {
    private volatile BacktraceCredentials credentials;
    private volatile BacktraceDatabaseConfig databaseConfig = new BacktraceDatabaseConfig();
    private volatile RequestHandler requestHandler;
    private volatile BeforeSendEvent beforeSendEvent;
    private volatile boolean gatherAllThreads = true;

    /**
     * Creates Backtrace credentials instance
     *
     * @param submissionUrl endpoint url address
     */
    public BacktraceConfig(String submissionUrl) {
        if (submissionUrl == null) {
            throw new NullPointerException("Endpoint URL can not be null");
        }
        credentials = new BacktraceCredentials(submissionUrl);
    }

    /**
     * Creates Backtrace credentials instance
     *
     * @param submissionUrl endpoint url address
     */
    public BacktraceConfig(URI submissionUrl) {
        if (submissionUrl == null) {
            throw new NullPointerException("Endpoint URL can not be null");
        }
        credentials = new BacktraceCredentials(submissionUrl);
    }

    /**
     * Creates Backtrace credentials instance
     *
     * @param endpointUrl     endpoint url address
     * @param submissionToken server access token
     */
    public BacktraceConfig(String endpointUrl, String submissionToken) {
        if (endpointUrl == null) {
            throw new NullPointerException("Endpoint URL can not be null");
        }

        if (submissionToken == null) {
            throw new NullPointerException("Submission token can not be null");
        }
        credentials = new BacktraceCredentials(endpointUrl, submissionToken);
    }

    /**
     * Returns instance of current database config
     *
     * @return Database config
     */
    public BacktraceDatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    /**
     * Returns Backtrace console server URL with parameters
     *
     * @return URL for Backtrace Console
     */
    String getSubmissionUrl() {
        return credentials.getSubmissionUrl().toString();
    }

    RequestHandler getRequestHandler() {
        return requestHandler;
    }

    BeforeSendEvent getBeforeSendEvent() {
        return beforeSendEvent;
    }

    boolean isGatherAllThreads() {
        return gatherAllThreads;
    }

    public void setGatherAllThreads(boolean gatherAllThreads) {
        this.gatherAllThreads = gatherAllThreads;
    }

    void setDatabasePath(String databasePath) {
        this.databaseConfig.setDatabasePath(databasePath);
    }

    void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    void setBeforeSendEvent(BeforeSendEvent beforeSendEvent) {
        this.beforeSendEvent = beforeSendEvent;
    }

    public void setDatabaseRetryLimit(int value) {
        this.databaseConfig.setDatabaseRetryLimit(value);
    }

    public void setMaxDatabaseSize(long value) {
        this.databaseConfig.setMaxDatabaseSize(value);
    }

    public void setMaxRecordCount(int value) {
        this.databaseConfig.setMaxRecordCount(value);
    }

    public void disableDatabase() {
        this.databaseConfig.disableDatabase();
    }

    public void enableDatabase() {
        this.databaseConfig.enableDatabase();
    }
}