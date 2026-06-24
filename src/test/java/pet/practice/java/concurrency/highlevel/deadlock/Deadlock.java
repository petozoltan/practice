package pet.practice.java.concurrency.highlevel.deadlock;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/// Both methods create the same a deadlock, but they close the ExecutorService differently.
public class Deadlock {

    @Test
    public void testDeadlockWithCancel() throws InterruptedException, ExecutionException {

        final ReentrantLock lockA = new ReentrantLock();
        final ReentrantLock lockB = new ReentrantLock();

        try (final ExecutorService executorService = Executors.newThreadPerTaskExecutor(Thread.ofPlatform().factory())) {

            final Future<?> futureAB = executorService.submit(() -> lock1ThenLock2(lockA, lockB));
            final Future<?> futureBA = executorService.submit(() -> lock1ThenLock2(lockB, lockA));

            getWithTimeout(futureAB);
            getWithTimeout(futureBA);

            // This is the test for the deadlock.
            assertFalse(futureAB.isDone());
            assertFalse(futureBA.isDone());

            // We need to interrupt the threads, otherwise ExecutorService auto-close will be blocked.
            // AutoCloseable.close() is called before the 'finally', so we cannot do this in the 'finally' clause.
            futureAB.cancel(true);
            futureBA.cancel(true);

            assumeTrue(futureAB.isDone());
            assumeTrue(futureBA.isDone());
        }
    }

    @Test
    public void testDeadlockWithShutdown() throws InterruptedException, ExecutionException {

        final ReentrantLock lockA = new ReentrantLock();
        final ReentrantLock lockB = new ReentrantLock();

        final ExecutorService executorService = Executors.newThreadPerTaskExecutor(Thread.ofPlatform().factory());

        try {
            final Future<?> futureAB = executorService.submit(() -> lock1ThenLock2(lockA, lockB));
            final Future<?> futureBA = executorService.submit(() -> lock1ThenLock2(lockB, lockA));

            getWithTimeout(futureAB);
            getWithTimeout(futureBA);

            // This is the test for the deadlock.
            assertFalse(futureAB.isDone());
            assertFalse(futureBA.isDone());

        } finally {
            // Normal shutdown() will not interrupt the threads.
            executorService.shutdownNow();
            executorService.awaitTermination(3, SECONDS);
        }
    }

    @Test
    public void testDeadlockWithVirtualThreads() throws InterruptedException, ExecutionException {

        final ReentrantLock lockA = new ReentrantLock();
        final ReentrantLock lockB = new ReentrantLock();

        final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

        try {
            final Future<?> futureAB = executorService.submit(() -> lock1ThenLock2(lockA, lockB));
            final Future<?> futureBA = executorService.submit(() -> lock1ThenLock2(lockB, lockA));

            getWithTimeout(futureAB);
            getWithTimeout(futureBA);

            // This is the test for the deadlock.
            assertFalse(futureAB.isDone());
            assertFalse(futureBA.isDone());

        } finally {
            // Normal shutdown() will not interrupt the threads.
            executorService.shutdownNow();
            executorService.awaitTermination(3, SECONDS);
        }
    }

    static void lock1ThenLock2(final ReentrantLock lock1, final ReentrantLock lock2) {
        try {
            // We must use lockInterruptibly() instead of lock(), otherwise the threads cannot be interrupted.
            lock1.lockInterruptibly();
            printLock(lock1, "1st", true);

            Thread.sleep(1000);

            lock2.lockInterruptibly();
            printLock(lock2, "2nd", true);

            // The place of the code that needs both locks...

        } catch (final InterruptedException e) {
            // We cannot throw the checked exception from Runnable.run() because it is not declared.
            System.err.println(Thread.currentThread().getName() + " has been interrupted.");

        } finally {
            unlock(lock1);
            unlock(lock2);
        }
    }

    static void unlock(final ReentrantLock lock) {
        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    public static void getWithTimeout(final Future<?> future) throws ExecutionException, InterruptedException {
        try {
            future.get(3, SECONDS);
        } catch (TimeoutException e) {
            System.err.println("Timeout waiting for future to complete.");
        }
    }

    public static void printLock(ReentrantLock lock, String tryNr, boolean locked) {
        System.out.println("Lock: " + lock.toString() +
                ", Thread: " + Thread.currentThread().getName() +
                ", tryNr: " + tryNr +
                ", tryLock: " + locked +
                ", isLocked: " + lock.isLocked() +
                ", isHeldByCurrentThread: " + lock.isHeldByCurrentThread() +
                ", getHoldCount: " + lock.getHoldCount());
    }
}