package backtrace.io;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Backtrace stack trace
 */
class BacktraceStackTrace implements Serializable {

    /**
     * Current exception
     */
    private Exception exception;

    /**
     * Collection of stacktrace elements
     */
    private ArrayList<BacktraceStackFrame> stackFrames = new ArrayList<>();

    private transient final static String NAME = "backtrace";

    /**
     * Create new instance of BacktraceStackTrace object
     *
     * @param exception current exception
     */
    BacktraceStackTrace(Exception exception) {
        this.exception = exception;
        this.stackFrames = gatherStacktraceInformation();
    }

    ArrayList<BacktraceStackFrame> getStackFrames() {
        return stackFrames;
    }

    /**
     * Get collection of stacktrace elements from current exception/thread
     * after filtering out frames from inside the Backtrace library
     * @return collection of stacktrace frames
     */
    private ArrayList<BacktraceStackFrame> gatherStacktraceInformation() {
        StackTraceElement[] stackTraceElements = this.exception != null ?
                this.exception.getStackTrace() : Thread.currentThread().getStackTrace();
        
        if (stackTraceElements == null || stackTraceElements.length == 0) {
            return null;
        }

        ArrayList<BacktraceStackFrame> result = new ArrayList<>();
        for (StackTraceElement frame : stackTraceElements) {
            if ((frame == null) || (frame.getClassName().isEmpty() && frame.getClassName().toLowerCase().startsWith(NAME)) ||
                    (frame.getClassName().toLowerCase().equals("java.lang.thread") && frame.getMethodName().equals("getStackTrace"))) {
                continue;
            }
            result.add(new BacktraceStackFrame(frame));
        }
        
        return result;
    }
}
