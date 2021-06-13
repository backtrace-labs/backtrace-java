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
        System.out.println("addWithLock");
        LOGGER.debug("Lock processing semaphore");
        System.out.println("Lock processing semaphore");
        this.lock();

        LOGGER.debug("Add message to the queue with lock");
        System.out.println("Add message to the queue with lock");
        this.add(message);

        LOGGER.debug("Queue not empty - release lock");
        System.out.println("Queue not empty - release lock");
        this.queueNotEmpty();
    }

    /**
     * Unlock semaphore to inform that all messages from queue are sent
     */
    void unlock() {
        LOGGER.debug("Releasing semaphore..");
        System.out.println("Releasing semaphore..");

        if (lock.getCount() == 1) {
            lock.countDown();
        }
    }

    /**
     * Lock semaphore because queue is empty
     */
    void queueIsEmpty() {
        LOGGER.debug("Queue is empty, counter: " + notEmptyQueue.getCount());
        System.out.println("Queue is empty, counter: " + notEmptyQueue.getCount());
        if(notEmptyQueue.getCount() == 0) {
            LOGGER.debug("Queue is empty - locking thread semaphore");
            notEmptyQueue.countUp();
        }
    }

    /**
     * Unlocking processing because queue is not empty
     */
    private void queueNotEmpty() {
        LOGGER.debug("Queue is NOT empty, counter: " + notEmptyQueue.getCount());
        System.out.println("Queue is NOT empty, counter: " + notEmptyQueue.getCount());
        if(notEmptyQueue.getCount() == 1) {
            LOGGER.debug("Queue is not empty - releasing semaphore");
            notEmptyQueue.countDown();
        }
    }

    /**
     * Lock semaphore to inform that at least one of messages are processing
     */
    private void lock() {
        if (lock.getCount() == 0){
            LOGGER.debug("Locking semaphore..");
            System.out.println("Locking semaphore..");
            lock.countUp();
            LOGGER.debug("Semaphore locked..");
            System.out.println("Semaphore locked..");
        }
    }

    /**
     * Wait until all messages in queue will be sent
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    void await() throws InterruptedException {
        LOGGER.debug("Waiting for the semaphore");
        System.out.println("Waiting for the semaphore");
        lock.await();
        LOGGER.debug("The semaphore has been released");
        System.out.println("The semaphore has been released");
    }

    void close() {
        LOGGER.debug("Closing queue - releasing semaphore");
        System.out.println("Closing queue - releasing semaphore");
        queueNotEmpty();
    }

    /**
     * Wait until new message will be added to the queue
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    void awaitNewMessage() throws InterruptedException {
        LOGGER.debug("Waiting until queue will not be empty");
        System.out.println("Waiting until queue will not be empty");
        notEmptyQueue.await();
        LOGGER.debug("Queue is not empty");
        System.out.println("Queue is not empty");
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
        System.out.println("Waiting for the semaphore");
        boolean result = lock.await(timeout, unit);
        LOGGER.debug("The semaphore has been released");
        System.out.println("The semaphore has been released");
        return result;
    }
}
