package backtrace.io;

import backtrace.io.helpers.CountLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class represents queue with locking mechanism to inform
 * that there are messages in queue or all messages from queue are sent
 */
class BacktraceQueue extends ConcurrentLinkedQueue<BacktraceMessage> {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(BacktraceQueue.class);
    private final CountLatch lock = new CountLatch(0, 0);
    private final CountLatch notEmptyQueue = new CountLatch(1, 0);

    /**
     * Add message to queue with locking semaphore to inform that at least one of messages are processing
     *
     * @param message error report
     */
    void addWithLock(BacktraceMessage message) {
        this.lock();
        this.add(message);
    }

    /**
     * Unlock semaphore to inform that all messages from queue are sent
     */
    void unlock() {
        LOGGER.debug("Releasing semaphore..");

        if(notEmptyQueue.getCount() == 0) {
            notEmptyQueue.countUp();
        }

        if (lock.getCount() == 0) {
            return;
        }

        lock.countDown();
    }

    /**
     * Lock semaphore to inform that at least one of messages are processing
     */
    private void lock() {
        if(notEmptyQueue.getCount() == 1) {
            notEmptyQueue.countDown();
        }

        if (lock.getCount() != 0) {
            return;
        }
        
        LOGGER.debug("Locking semaphore..");
        lock.countUp();
        LOGGER.debug("Semaphore locked..");
    }

    /**
     * Wait until all messages in queue will be sent
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    void await() throws InterruptedException {
        LOGGER.debug("Waiting for the semaphore");
        lock.await();
        LOGGER.debug("The semaphore has been released");
    }

    void close() {
        if(notEmptyQueue.getCount() == 1) {
            notEmptyQueue.countDown();
        }
    }

    /**
     * Wait until all messages in queue will be sent
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    void awaitNewMessage() throws InterruptedException {
        LOGGER.debug("Waiting until queue will not be empty");
        notEmptyQueue.await();
        LOGGER.debug("Queue is not empty");
    }


    /**
     * Wait until all messages in queue will be sent
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the {@code timeout} argument
     * @return {@code true} if all messages are sent in passed time and {@code false}
     * if the waiting time elapsed before all messages has been sent
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    boolean await(long timeout,
                  TimeUnit unit) throws InterruptedException {
        LOGGER.debug("Waiting for the semaphore");
        boolean result = lock.await(timeout, unit);
        LOGGER.debug("The semaphore has been released");
        return result;
    }
}
