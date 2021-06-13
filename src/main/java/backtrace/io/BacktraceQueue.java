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
    private final CountLatch processingLock = new CountLatch(0, 0); // state 1 if processing
    private final CountLatch notEmptyQueueLock = new CountLatch(1, 0); // state 1 if queue is empty
    private final CountLatch closingLock = new CountLatch(1, 0); // state 0 if closing

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
        System.out.println("Releasing semaphore.. " + processingLock.getCount());

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
        System.out.println("Queue is empty, notEmptyQueue counter: " + notEmptyQueueLock.getCount());
        if(notEmptyQueueLock.getCount() == 0) {
            LOGGER.debug("Queue is empty - locking thread semaphore");
            notEmptyQueueLock.countUp();
        }
    }

    /**
     * Unlocking processing because queue is not empty
     */
    private void queueNotEmpty() {
        LOGGER.debug("Queue is NOT empty, notEmptyQueue counter: " + notEmptyQueueLock.getCount());
        System.out.println("Queue is NOT empty, notEmptyQueue counter: " + notEmptyQueueLock.getCount());
        if(notEmptyQueueLock.getCount() == 1) {
            LOGGER.debug("Queue is not empty - releasing semaphore");
            notEmptyQueueLock.countDown();
        }
    }

    /**
     * Lock semaphore to inform that at least one of messages are processing
     */
    private void lock() {
        if (processingLock.getCount() == 0){
            LOGGER.debug("Locking semaphore..");
            System.out.println("Locking semaphore.. " + processingLock.getCount());
            processingLock.countUp();
            LOGGER.debug("Semaphore locked..");
            System.out.println("Semaphore locked.. " + processingLock.getCount());
        }
    }

    /**
     * Wait until all messages in queue will be sent
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    void await() throws InterruptedException {
        LOGGER.debug("Waiting for the semaphore");
        System.out.println("Waiting for the semaphore " + processingLock.getCount());
        processingLock.await();
        LOGGER.debug("The semaphore has been released " + processingLock.getCount());
        System.out.println("The semaphore has been released");
    }

    boolean isClosing() {
        return closingLock.getCount() == 0;
    }

    void close() {
        LOGGER.debug("Closing queue - releasing semaphore");
        System.out.println("Closing queue - releasing semaphore " + processingLock.getCount());
        if(closingLock.getCount() == 1) {
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
        System.out.println("Waiting until queue will not be empty " + notEmptyQueueLock.getCount());
        notEmptyQueueLock.await();
        LOGGER.debug("Queue is not empty");
        System.out.println("Queue is not empty " + notEmptyQueueLock.getCount());
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
        System.out.println("Waiting for the semaphore " + processingLock.getCount());
        boolean result = processingLock.await(timeout, unit);
        LOGGER.debug("The semaphore has been released " + processingLock.getCount());
        System.out.println("The semaphore has been released");
        return result;
    }
}
