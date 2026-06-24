package pet.practice.java.concurrency.highlevel.deadlock;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static pet.practice.java.concurrency.highlevel.deadlock.Deadlock.getWithTimeout;
import static pet.practice.java.concurrency.highlevel.deadlock.Deadlock.printLock;

public class DeadlockPrevention {

    static final ReentrantLock LOCK_A = new ReentrantLock();
    static final ReentrantLock LOCK_B = new ReentrantLock();

    @Test
    public void testDeadlockPrevention() throws InterruptedException, ExecutionException {

        final ReentrantLock lockA = new ReentrantLock();
        final ReentrantLock lockB = new ReentrantLock();

        try (final ExecutorService executorService = Executors.newThreadPerTaskExecutor(Thread.ofPlatform().factory())) {

            final Future<?> futureAB = executorService.submit(() -> lock1ThenLock2(lockA, lockB));
            final Future<?> futureBA = executorService.submit(() -> lock1ThenLock2(lockB, lockA));

            getWithTimeout(futureAB);
            getWithTimeout(futureBA);

            // This is the test for the no-deadlock.
            assertTrue(futureAB.isDone());
            assertTrue(futureBA.isDone());
        }
    }

    static void lock1ThenLock2(final ReentrantLock lock1, final ReentrantLock lock2) {

        boolean locked1 = false;
        boolean locked2 = false;

        try {
            locked1 = lock1.tryLock();
            printLock(lock1, "1st", locked1);

            Thread.sleep(1000);

            locked2 = lock2.tryLock();
            printLock(lock2, "2nd", locked2);

            // The place of the code that needs both locks...

        } catch (final InterruptedException e) {
            // We cannot throw the checked exception from Runnable.run() because it is not declared.
            System.err.println(Thread.currentThread().getName() + " has been interrupted.");

        } finally {
            if (locked1) {
                lock1.unlock();
            }
            if (locked2) {
                lock2.unlock();
            }
        }
    }
}