package backtrace.io;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Backtrace stack trace
 */
public class BacktraceStackTrace implements Serializable {

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
        initialize();
    }

    ArrayList<BacktraceStackFrame> getStackFrames() {
        return stackFrames;
    }

    public Exception getException() {
        return exception;
    }

    private void initialize() {
        StackTraceElement[] stackTraceElements = this.exception != null ?
                this.exception.getStackTrace() : Thread.currentThread().getStackTrace();
        if (stackTraceElements == null || stackTraceElements.length == 0) {
            return;
        }
        setStacktraceInformation(stackTraceElements);
    }

    private void setStacktraceInformation(StackTraceElement[] frames) {
        if (frames == null || frames.length == 0) {
            return;
        }

        for (StackTraceElement frame : frames) {
            if ((frame == null) || (frame.getClassName().isEmpty() && frame.getClassName().toLowerCase().startsWith(NAME)) ||
                    (frame.getClassName().toLowerCase().equals("java.lang.thread") && frame.getMethodName().equals("getStackTrace"))) {
                continue;
            }
            this.stackFrames.add(new BacktraceStackFrame(frame));
        }
    }
}
