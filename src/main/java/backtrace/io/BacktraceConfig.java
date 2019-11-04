package backtrace.io;

public class BacktraceConfig {
    private BacktraceCredentials credentials;
    private BacktraceDatabaseConfig databaseConfig;
    private final String FORMAT = "json";
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
     *
     * @return
     */
    public BacktraceDatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    /**
     * Get URL to Backtrace server API
     *
     * @return endpoint url
     */
    private String getEndpointUrl() {
        return credentials.getEndpointUrl();
    }

    /**
     * Get an access token to Backtrace server API
     *
     * @return access token
     */
    private String getSubmissionToken() {
        return credentials.getSubmissionToken();
    }

    /**
     * Get Backtrace console server URL with parameters
     */
    String getServerUrl() {
        return String.format("%spost?format=%s&token=%s", getEndpointUrl(), FORMAT, getSubmissionToken());
    }
}