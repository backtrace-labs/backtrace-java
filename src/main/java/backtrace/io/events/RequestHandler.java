package backtrace.io.events;


import backtrace.io.data.BacktraceData;
import backtrace.io.http.BacktraceResult;

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
