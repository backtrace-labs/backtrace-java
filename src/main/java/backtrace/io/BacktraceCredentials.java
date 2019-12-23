package backtrace.io;


import java.net.URI;

class BacktraceCredentials {
    @SuppressWarnings("FieldCanBeLocal")
    private final static String FORMAT = "json";

    private URI backtraceHostUri;

    /**
     * Creates Backtrace credentials instance
     *
     * @param endpointUrl     endpoint url address
     * @param submissionToken server access token
     */
    BacktraceCredentials(String endpointUrl, String submissionToken) {
        this(String.format("%spost?format=%s&token=%s", endpointUrl, FORMAT, submissionToken));
    }

    /**
     * Creates Backtrace credentials instance
     *
     * @param backtraceHostUri submission uri string
     */
    BacktraceCredentials(String backtraceHostUri) {
        this(URI.create(backtraceHostUri));
    }

    /**
     * Creates Backtrace credentials instance
     *
     * @param backtraceHostUri submission uri
     */
    BacktraceCredentials(URI backtraceHostUri) {
        this.backtraceHostUri = backtraceHostUri;
    }

    /**
     * Get submission URL to Backtrace API
     *
     * @return URL to Backtrace API
     */
    URI getSubmissionUrl() {
        return backtraceHostUri;
    }
}