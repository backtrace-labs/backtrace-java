package backtrace.io;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;


/**
 * Backtrace stack frame
 */
public class BacktraceStackFrame implements Serializable {

    /**
     * Function where exception occurs
     */
    @SerializedName("funcName")
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    private String functionName;


    /**
     * Line number in source code where exception occurs
     */
    @SerializedName("line")
    @SuppressWarnings("FieldCanBeLocal")
    private Integer line = null;

    /**
     * Source code file name where exception occurs
     */
    @SerializedName("sourceCode")
    @SuppressWarnings("FieldCanBeLocal")
    private String sourceCode;

    /**
     * Source code file name where exception occurs
     */
    @SuppressWarnings("FieldCanBeLocal")
    private transient String sourceCodeFileName;

    /**
     * Create new instance of BacktraceStackFrame
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public BacktraceStackFrame() {
    }

    /**
     * Create new instance of BacktraceStackFrame
     *
     * @param frame single stacktrace element
     */
    BacktraceStackFrame(StackTraceElement frame) {
        if (frame == null) {
            return;
        }
        this.functionName = frame.getClassName() + "." + frame.getMethodName();
        this.sourceCodeFileName = frame.getFileName();
        this.sourceCode = UUID.randomUUID().toString();
        this.line = frame.getLineNumber() > 0 ? frame.getLineNumber() : null;
    }

    Integer getLine() {
        return line;
    }

    String getSourceCode() {
        return sourceCode;
    }

    String getSourceCodeFileName() {
        return sourceCodeFileName;
    }
}