package backtrace.io;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * Single instance of source data frame
 */
class SourceCode implements Serializable {
    /**
     * Line number in source code where exception occurs
     */
    @SerializedName("startLine")
    @SuppressWarnings("FieldCanBeLocal")
    private Integer startLine;

    /**
     * Filename to source file where exception occurs
     */
    @SerializedName("path")
    @SuppressWarnings("FieldCanBeLocal")
    private String sourceCodeFileName;

    /**
     * Create SourceCode instance
     * @param stackFrame current StackFrame
     */
    SourceCode(BacktraceStackFrame stackFrame) {
        this.sourceCodeFileName = stackFrame.getSourceCodeFileName();
        this.startLine = stackFrame.getLine();
    }
}
