package backtrace.io.events;


import backtrace.io.BacktraceData;
import backtrace.io.BacktraceResult;

/**
 * Interface definition for a callback to be invoked instead of default request to Backtrace API
 */
public interface RequestHandler {
    /**
     * Event which will be executed instead of default request to Backtrace API
     *
     * @param data which should be send to Backtrace API
     * @return response on request
     */
    BacktraceResult onRequest(BacktraceData data);
}
