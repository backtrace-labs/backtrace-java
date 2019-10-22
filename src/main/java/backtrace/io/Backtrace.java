package backtrace.io;

import java.util.concurrent.ConcurrentLinkedQueue;

class Backtrace {

    ConcurrentLinkedQueue<Integer> queue;
    BacktraceThread thread;
    Backtrace(){

        queue = new ConcurrentLinkedQueue();
        this.queue = queue;

        thread = new BacktraceThread(queue);
        thread.start();
    }

    public void addElement(Integer x){
        queue.add(x);
    }
}
