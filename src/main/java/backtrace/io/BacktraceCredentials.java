package backtrace.io;


class BacktraceCredentials {
    private String endpointUrl;
    private String submissionToken;

    /**
     * Initialize Backtrace credentials
     *
     * @param endpointUrl     endpoint url address
     * @param submissionToken server access token
     */
    BacktraceCredentials(String endpointUrl, String submissionToken) {
        this.endpointUrl = endpointUrl;
        this.submissionToken = submissionToken;
    }

    /**
     * Get URL to Backtrace server API
     *
     * @return endpoint url
     */
    String getEndpointUrl() {
        return endpointUrl;
    }

    /**
     * Get an access token to Backtrace server API
     *
     * @return access token
     */
    String getSubmissionToken() {
        return submissionToken;
    }
}