package backtrace.io;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BacktraceThread extends Thread {

    private ConcurrentLinkedQueue<Integer> queue;

    public BacktraceThread(ConcurrentLinkedQueue<Integer> queue){
        super();
        this.queue = queue;
    }

    @Override
    public void run(){
        while(true){
            Integer x = queue.poll();
            if (x == null){
                continue;
            }
            System.out.println(x);
        }
    }
}