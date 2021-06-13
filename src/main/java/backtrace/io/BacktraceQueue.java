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
    private final CountLatch processingLock = new CountLatch(0, 0); // state 1 if should process message
    private final CountLatch notEmptyQueueLock = new CountLatch(1, 0); // state 1 if queue is empty
    private final CountLatch closingLock = new CountLatch(1, 0); // state 0 if closing

    /**
     * Add message to queue with locking semaphore to inform that at least one of messages are processing
     *
     * @param message error report
     */
    void addWithLock(BacktraceMessage message) {
        LOGGER.debug("Lock processing semaphore");
        this.lock();

        LOGGER.debug("Add message to the queue with lock");
        this.add(message);

        LOGGER.debug("Queue not empty - release lock");
        this.queueNotEmpty();
    }

    /**
     * Unlock semaphore to inform that all messages from queue are sent
     */
    void unlock() {
        LOGGER.debug("Releasing semaphore..");

        if (processingLock.getCount() == 1) {
            processingLock.countDown();
        }
    }

    boolean shouldHandleMessages() {
        return processingLock.getCount() == 1;
    }

    /**
     * Lock semaphore because queue is empty
     */
    void queueIsEmpty() {
        LOGGER.debug("Queue is empty, notEmptyQueue counter: " + notEmptyQueueLock.getCount());
        if (notEmptyQueueLock.getCount() == 0) {
            LOGGER.debug("Queue is empty - locking thread semaphore");
            notEmptyQueueLock.countUp();
        }
    }

    /**
     * Unlocking processing because queue is not empty
     */
    private void queueNotEmpty() {
        LOGGER.debug("Queue is NOT empty, notEmptyQueue counter: " + notEmptyQueueLock.getCount());
        if (notEmptyQueueLock.getCount() == 1) {
            LOGGER.debug("Queue is not empty - releasing semaphore");
            notEmptyQueueLock.countDown();
        }
    }

    /**
     * Lock semaphore to inform that at least one of messages are processing
     */
    private void lock() {
        if (processingLock.getCount() == 0) {
            LOGGER.debug("Locking semaphore..");
            processingLock.countUp();
            LOGGER.debug("Semaphore locked..");
        }
    }

    /**
     * Wait until all messages in queue will be sent
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    void await() throws InterruptedException {
        LOGGER.debug("Waiting for the semaphore");
        processingLock.await();
        LOGGER.debug("The semaphore has been released " + processingLock.getCount());
    }

    boolean isClosing() {
        return closingLock.getCount() == 0;
    }

    void close() {
        LOGGER.debug("Closing queue - releasing semaphore");
        if (closingLock.getCount() == 1) {
            closingLock.countDown();
        }
        queueNotEmpty();
    }

    /**
     * Wait until new message will be added to the queue
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    void awaitNewMessage() throws InterruptedException {
        LOGGER.debug("Waiting until queue will not be empty");
        notEmptyQueueLock.await();
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
        boolean result = processingLock.await(timeout, unit);
        LOGGER.debug("The semaphore has been released " + processingLock.getCount());
        return result;
    }
}
