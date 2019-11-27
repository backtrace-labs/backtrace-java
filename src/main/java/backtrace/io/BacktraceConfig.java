package backtrace.io;

import backtrace.io.events.BeforeSendEvent;
import backtrace.io.events.RequestHandler;

public class BacktraceConfig {
    private BacktraceCredentials credentials;
    private BacktraceDatabaseConfig databaseConfig = new BacktraceDatabaseConfig();
    private RequestHandler requestHandler;
    private BeforeSendEvent beforeSendEvent;

    @SuppressWarnings("FieldCanBeLocal")
    private final String FORMAT = "json";

    /**
     * Creates Backtrace credentials instance
     *
     * @param endpointUrl     endpoint url address
     * @param submissionToken server access token
     */
    public BacktraceConfig(String endpointUrl, String submissionToken) {
        if (endpointUrl == null){
            throw new NullPointerException("Endpoint URL can not be null");
        }

        if (submissionToken == null){
            throw new NullPointerException("Submission token can not be null");
        }
        credentials = new BacktraceCredentials(endpointUrl, submissionToken);
    }

    /**
     * Returns instance of current database config
     * @return Database config
     */
    BacktraceDatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    /**
     * Returns URL to Backtrace server API
     *
     * @return Endpoint url
     */
    private String getEndpointUrl() {
        return credentials.getEndpointUrl();
    }

    /**
     * Returns an access token to Backtrace server API
     *
     * @return Access token
     */
    private String getSubmissionToken() {
        return credentials.getSubmissionToken();
    }

    /**
     * Returns Backtrace console server URL with parameters
     * @return URL for Backtrace Console
     */
    String getServerUrl() {
        return String.format("%spost?format=%s&token=%s", getEndpointUrl(), FORMAT, getSubmissionToken());
    }

    RequestHandler getRequestHandler() {
        return requestHandler;
    }

    BeforeSendEvent getBeforeSendEvent() {
        return beforeSendEvent;
    }

    void setDatabasePath(String databasePath){
        this.databaseConfig.setDatabasePath(databasePath);
    }

    void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    void setBeforeSendEvent(BeforeSendEvent beforeSendEvent) {
        this.beforeSendEvent = beforeSendEvent;
    }

    public void setDatabaseRetryLimit(int value){
        this.databaseConfig.setDatabaseRetryLimit(value);
    }

    public void setMaxDatabaseSize(long value){
        this.databaseConfig.setMaxDatabaseSize(value);
    }

    public void setMaxRecordCount(int value){
        this.databaseConfig.setMaxRecordCount(value);
    }

    public void disableDatabase(){
        this.databaseConfig.disableDatabase();
    }
}