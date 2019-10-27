package backtrace.io.temp;

/**
 * Existing send method result statuses
 */
public enum BacktraceResultStatus {
    /**
     * Set when error occurs while sending diagnostic data
     */
    ServerError,

    /**
     * Set when data were send to API
     */
    Ok,
}