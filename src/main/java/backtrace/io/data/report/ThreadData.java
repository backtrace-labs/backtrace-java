package backtrace.io.data.report;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Generate information about application threads
 */
public class ThreadData implements Serializable {

    /**
     * All collected application threads information
     */
    Map<String, ThreadInformation> threadInformation = new HashMap<>();

    /**
     * Get main thread id
     *
     * @return main thread id
     */
    public String getMainThread() {
        return mainThread;
    }

    public Map<String, ThreadInformation> getThreadInformation() {
        return threadInformation;
    }

    /**
     * Application Id for current thread.
     * This value is used in mainThreadSection in output JSON file
     */
    private String mainThread = "";

    /**
     * Create instance of ThreadData class to collect information about used threads
     *
     * @param exceptionStack current BacktraceReport exception stack
     * @param allThreads     if true information about all threads will be gathered
     */
    public ThreadData(ArrayList<BacktraceStackFrame> exceptionStack, boolean allThreads) {
        generateCurrentThreadInformation(exceptionStack);
        if (allThreads) {
            processThreads();
        }
    }

    /**
     * Generate information for current thread
     *
     * @param exceptionStack current BacktraceReport exception stack
     */
    private void generateCurrentThreadInformation(ArrayList<BacktraceStackFrame> exceptionStack) {
        Thread currThread = Thread.currentThread();
        mainThread = currThread.getName().toLowerCase();
        this.threadInformation.put(mainThread,
                new ThreadInformation(currThread, exceptionStack, true)
        );
    }

    /**
     * Process all threads and save information about thread and stacktrace
     */
    private void processThreads() {
        Map<Thread, StackTraceElement[]> myMap = Thread.getAllStackTraces();

        for (Map.Entry<Thread, StackTraceElement[]> entry : myMap.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            Thread thread = entry.getKey();
            String threadName = thread.getName().toLowerCase();

            if (this.getMainThread().equals(threadName)) {
                continue;
            }

            StackTraceElement[] stack = entry.getValue();
            ArrayList<BacktraceStackFrame> stackFrame = new ArrayList<>();
            if (stack != null && stack.length != 0) {
                for (StackTraceElement stackTraceElement : stack) {
                    stackFrame.add(new BacktraceStackFrame(stackTraceElement));
                }
            }
            this.threadInformation.put(threadName, new ThreadInformation(thread, stackFrame, false));
        }
    }
}
