package backtrace.io;

import backtrace.io.helpers.CountLatch;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class represents queue with locking mechanism to inform
 * that there are messages in queue or all messages from queue are sent
 */
class BacktraceQueue extends ConcurrentLinkedQueue<BacktraceMessage> {

    private final CountLatch processingLock = new CountLatch(0, 0); // state 1 if should process message
    private final CountLatch notEmptyQueueLock = new CountLatch(1, 0); // state 1 if queue is empty
    private final CountLatch closingLock = new CountLatch(1, 0); // state 0 if closing

    /**
     * Add message to queue with locking semaphore to inform that at least one of messages are processing
     *
     * @param message error report
     */
    void addWithLock(BacktraceMessage message) {
        this.lock();

        this.add(message);

        this.queueNotEmpty();
    }

    /**
     * Unlock semaphore to inform that all messages from queue are sent
     */
    void unlock() {
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
        if (notEmptyQueueLock.getCount() == 0) {
            notEmptyQueueLock.countUp();
        }
    }

    /**
     * Unlocking processing because queue is not empty
     */
    private void queueNotEmpty() {
        if (notEmptyQueueLock.getCount() == 1) {
            notEmptyQueueLock.countDown();
        }
    }

    /**
     * Lock semaphore to inform that at least one of messages are processing
     */
    private void lock() {
        if (processingLock.getCount() == 0) {
            processingLock.countUp();
        }
    }

    /**
     * Wait until all messages in queue will be sent
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    void await() throws InterruptedException {
        processingLock.await();
    }

    boolean isClosing() {
        return closingLock.getCount() == 0;
    }

    void close() {
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
        notEmptyQueueLock.await();
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
        boolean result = processingLock.await(timeout, unit);
        return result;
    }
}
