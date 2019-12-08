package backtrace.io;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Get an information about single thread passed in constructor
 */
class ThreadInformation implements Serializable {
    /**
     * Thread name
     */
    @SerializedName("name")
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    private String name;

    /**
     * Denotes whether a thread is a faulting thread
     */
    @SerializedName("fault")
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    private Boolean fault;


    /**
     * Current thread stacktrace
     */
    @SerializedName("stack")
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    private ArrayList<BacktraceStackFrame> stack;

    /**
     * Create new instance of ThreadInformation
     *
     * @param threadName thread name
     * @param fault      denotes whether a thread is a faulting thread - in most cases main thread
     * @param stack      exception stack information
     */
    private ThreadInformation(String threadName, Boolean fault, ArrayList<BacktraceStackFrame>
            stack) {
        this.stack = stack == null ? new ArrayList<>() : stack;
        this.name = threadName;
        this.fault = fault;
    }

    /**
     * Create new instance of ThreadInformation
     *
     * @param thread        thread to analyse
     * @param stack         exception stack information
     * @param currentThread is current thread flag
     */
    ThreadInformation(Thread thread, ArrayList<BacktraceStackFrame> stack, Boolean currentThread) {
        this(thread.getName().toLowerCase(), currentThread, stack);
    }
}
