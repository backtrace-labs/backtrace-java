package backtrace.io;

import backtrace.io.helpers.CountLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

class BacktraceQueue extends ConcurrentLinkedQueue<BacktraceMessage> {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(BacktraceQueue.class);
    private final CountLatch lock = new CountLatch(1, 0);

    void release() {
        if (lock.getCount() == 0) {
            return;
        }
        LOGGER.debug("Releasing semaphore..");
        lock.countDown();
    }

    void lock() {
        if (lock.getCount() != 0) {
            return;
        }
        LOGGER.debug("Locking semaphore..");
        lock.countUp();
        LOGGER.debug("Semaphore locked..");
    }

    void await() throws InterruptedException {
        LOGGER.debug("Waiting for semaphore");
        lock.await();
        LOGGER.debug("After waiting for semaphore");
    }

    void await(long timeout,
               TimeUnit unit) throws InterruptedException {
        LOGGER.debug("Waiting for semaphore");
        lock.await(timeout, unit);
        LOGGER.debug("After waiting for semaphore");
    }
}
