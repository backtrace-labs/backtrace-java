package backtrace.io;


class BacktraceCredentials {
    private String endpointUrl;
    private String submissionToken;

    /**
     * Creates Backtrace credentials instance
     *
     * @param endpointUrl     endpoint url address
     * @param submissionToken server access token
     */
    BacktraceCredentials(String endpointUrl, String submissionToken) {
        this.endpointUrl = endpointUrl;
        this.submissionToken = submissionToken;
    }

    /**
     * Returns URL to Backtrace server API
     *
     * @return Endpoint url
     */
    String getEndpointUrl() {
        return endpointUrl;
    }

    /**
     * Returns an access token to Backtrace server API
     *
     * @return Access token
     */
    String getSubmissionToken() {
        return submissionToken;
    }
}