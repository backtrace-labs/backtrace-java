package backtrace.io;

import java.util.concurrent.ConcurrentLinkedQueue;

class Backtrace {
    ConcurrentLinkedQueue queue;

    Backtrace(ConcurrentLinkedQueue queue){
        this.queue = queue;
    }
}
