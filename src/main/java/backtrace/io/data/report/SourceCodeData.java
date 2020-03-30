package backtrace.io.data.report;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Collect all source data information about current program
 */
public class SourceCodeData implements Serializable {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(backtrace.io.data.report.SourceCodeData.class);

    /**
     * Source code information about current executed program
     */
    public Map<String, SourceCode> data = new HashMap<>();

    public SourceCodeData(ArrayList<BacktraceStackFrame> exceptionStack) {
        LOGGER.debug("Initialization source code data");
        if (exceptionStack == null || exceptionStack.size() == 0) {
            LOGGER.warn("Exception stack is null or empty");
            return;
        }

        for (BacktraceStackFrame stackFrame : exceptionStack) {
            if (stackFrame == null || stackFrame.getSourceCode().equals("")) {
                LOGGER.warn("Stack frame is null or sourceCode is empty");
                continue;
            }
            String id = stackFrame.getSourceCode();
            SourceCode value = new SourceCode(stackFrame);
            data.put(id, value);
        }
    }
}