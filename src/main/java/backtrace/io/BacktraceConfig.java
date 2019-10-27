package backtrace.io;

public class BacktraceConfig {
    private BacktraceCredentials credentials;
    private BacktraceDatabaseConfig databaseConfig;

    /**
     * Initialize Backtrace credentials
     *
     * @param endpointUrl endpoint url address
     * @param submissionToken server access token
     */
    public BacktraceConfig(String endpointUrl, String submissionToken) {
        credentials = new BacktraceCredentials(endpointUrl, submissionToken);
    }

    /**
     * Get URL to Backtrace server API
     *
     * @return endpoint url
     */
    public String getEndpointUrl() {
        return credentials.getEndpointUrl();
    }

    /**
     * Get an access token to Backtrace server API
     *
     * @return access token
     */
    public String getSubmissionToken() {
        return credentials.getSubmissionToken();
    }

}