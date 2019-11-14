package backtrace.io;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Collect all source data information about current program
 */
public class SourceCodeData implements Serializable {
    private static transient final String LOG_TAG = SourceCodeData.class.getSimpleName();

    /**
     * Source code information about current executed program
     */
    public Map<String, SourceCode> data = new HashMap<>();

    SourceCodeData(ArrayList<StackFrame> exceptionStack) {
//        BacktraceLogger.d(LOG_TAG, "Initialization source code data");
        if (exceptionStack == null || exceptionStack.size() == 0) {
//            BacktraceLogger.w(LOG_TAG, "Exception stack is null or empty");
            return;
        }

        for (StackFrame stackFrame : exceptionStack) {
            if (stackFrame == null || stackFrame.getSourceCode().equals("")) {
//                BacktraceLogger.w(LOG_TAG, "Stack frame is null or sourceCode is empty");
                continue;
            }
            String id = stackFrame.getSourceCode();
            SourceCode value = new SourceCode(stackFrame);
            data.put(id, value);
        }
    }
}