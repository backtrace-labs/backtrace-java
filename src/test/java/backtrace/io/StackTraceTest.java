package backtrace.io;

import backtrace.io.data.report.BacktraceStackFrame;
import backtrace.io.data.report.BacktraceStackTrace;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class StackTraceTest {

    @Test
    public void removeIncorrectPathsFromNull() {
        // GIVEN
        Exception testException = new Exception();
        StackTraceElement[] stackTraceElements = new StackTraceElement[2];
        stackTraceElements[0] = new StackTraceElement("BacktraceTest", "test", "test", 1);
        stackTraceElements[1] = new StackTraceElement("Demo", "test", "test", 1);
        testException.setStackTrace(stackTraceElements);

        // WHEN
        BacktraceStackTrace backtraceStackTrace = new BacktraceStackTrace(testException);

        // THEN
        Assert.assertEquals(1, backtraceStackTrace.getStackFrames().size());
    }

    @Test
    public void removeApacheLog4jLogs() {
        // GIVEN
        final String declaringClass = "Demo";
        Exception testException = new Exception();
        StackTraceElement[] stackTraceElements = new StackTraceElement[2];
        stackTraceElements[0] = new StackTraceElement("org.apache.log4j.AppenderSkeleton", "doAppend", "AppenderSkeleton.java", 1);
        stackTraceElements[1] = new StackTraceElement("Demo", "test", "test", 1);
        testException.setStackTrace(stackTraceElements);

        // WHEN
        BacktraceStackTrace backtraceStackTrace = new BacktraceStackTrace(testException);

        // THEN
        Assert.assertEquals(1, backtraceStackTrace.getStackFrames().size());
    }

    @Test
    public void removeApacheLog4j2Logs() {
        // GIVEN
        final String declaringClass = "Demo";
        Exception testException = new Exception();
        StackTraceElement[] stackTraceElements = new StackTraceElement[2];
        stackTraceElements[0] = new StackTraceElement("org.apache.logging.log4j.core.config.AppenderControl", "tryCallAppender", "AppenderControl.java", 1);
        stackTraceElements[1] = new StackTraceElement("Demo", "test", "test", 1);
        testException.setStackTrace(stackTraceElements);

        // WHEN
        BacktraceStackTrace backtraceStackTrace = new BacktraceStackTrace(testException);

        // THEN
        Assert.assertEquals(1, backtraceStackTrace.getStackFrames().size());
    }
}
