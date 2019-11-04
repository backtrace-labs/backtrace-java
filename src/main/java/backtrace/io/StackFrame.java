package backtrace.io;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;


/**
 * Backtrace stack frame
 */
public class StackFrame implements Serializable {

    /**
     * Function where exception occurs
     */
    @SerializedName("funcName")
    public String functionName;
    /**
     * Line number in source code where exception occurs
     */
    @SerializedName("line")
    public Integer line = null;

    /**
     * Source code file name where exception occurs
     */
    @SerializedName("sourceCode")
    public String sourceCode;

    /**
     * Source code file name where exception occurs
     */
    public transient String sourceCodeFileName;

    /**
     * Create new instance of BacktraceStackFrame
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public StackFrame() {
    }

    /**
     * Create new instance of BacktraceStackFrame
     *
     * @param frame single stacktrace element
     */
    public StackFrame(StackTraceElement frame) {
        if (frame == null || frame.getMethodName() == null) {
            return;
        }
        this.functionName = frame.getClassName() + "." + frame.getMethodName();
        this.sourceCodeFileName = frame.getFileName();
        this.sourceCode = UUID.randomUUID().toString();
        this.line = frame.getLineNumber() > 0 ? frame.getLineNumber() : null;
    }
}