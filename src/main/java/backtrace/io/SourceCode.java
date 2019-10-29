package backtrace.io;

import backtrace.io.temp.BacktraceStackFrame;
import com.google.gson.annotations.SerializedName;


/**
 * Single instance of source data frame
 */
public class SourceCode {
    /**
     * Line number in source code where exception occurs
     */
    @SerializedName("startLine")
    public Integer startLine;

    /**
     * Filename to source file where exception occurs
     */
    @SerializedName("path")
    public String sourceCodeFileName;


    public SourceCode(BacktraceStackFrame stackFrame) {
        this.sourceCodeFileName = stackFrame.sourceCodeFileName;
        this.startLine = stackFrame.line;
    }
}
