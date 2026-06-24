package pet.practice.java.concurrency.lowlevel.deadlock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Deadlock {

    static final String LOCK_A = "A";
    static final String LOCK_B = "B";

    static final Thread THREAD_AB = new Thread(() -> lock1ThenLock2(LOCK_A, LOCK_B));
    static final Thread THREAD_BA = new Thread(() -> lock1ThenLock2(LOCK_B, LOCK_A));

    @Test
    public void testDeadlock() throws InterruptedException {
        try {
            THREAD_AB.start();
            THREAD_BA.start();

            THREAD_AB.join(3000);
            THREAD_BA.join(3000);

            assertTrue(THREAD_AB.isAlive());
            assertTrue(THREAD_BA.isAlive());

        } finally {
            if (THREAD_AB.isAlive()) {
                THREAD_AB.interrupt();
            }
            if (THREAD_BA.isAlive()) {
                THREAD_BA.interrupt();
            }
        }
    }

    static void lock1ThenLock2(final Object lock1, final Object lock2) {
        try {
            synchronized (lock1) {
                printLock(lock1, "1st", true);

                Thread.sleep(1000);

                synchronized (lock2) {
                    printLock(lock2, "2nd", true);

                    // The place of the code that needs both locks...
                }
            }
        } catch (final InterruptedException e) {
            // We cannot throw the checked InterruptedException from Runnable.run() because it is not declared.
            System.err.println(Thread.currentThread().getName() + " has been interrupted.");
        }
    }

    static void printLock(Object lock, String lockNr, boolean locked) {
        System.out.println("Lock: " + lock.toString() +
                ", Thread: " + Thread.currentThread().getName() +
                ", lockNr: " + lockNr +
                ", locked: " + locked);
    }
}