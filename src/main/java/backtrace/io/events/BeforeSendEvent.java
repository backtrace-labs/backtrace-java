package backtrace.io.events;

import backtrace.io.data.BacktraceData;

/**
 * Interface definition for a callback to be invoked before send report to Backtrace API
 */
public interface BeforeSendEvent {
    /**
     * Event which will be executed before send report to Backtrace API
     *
     * @param data data which will be send to Backtrace API
     * @return data which should be send to Backtrace API
     */
    BacktraceData onEvent(BacktraceData data);
}