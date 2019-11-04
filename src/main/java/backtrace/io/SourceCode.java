package backtrace.io;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * Single instance of source data frame
 */
public class SourceCode implements Serializable {
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


    public SourceCode(StackFrame stackFrame) {
        this.sourceCodeFileName = stackFrame.sourceCodeFileName;
        this.startLine = stackFrame.line;
    }
}
