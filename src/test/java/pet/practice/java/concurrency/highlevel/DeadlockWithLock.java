package pet.practice.java.concurrency.highlevel;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeadlockWithLock {

    static final NamedLock LOCK_A = new NamedLock("A");
    static final NamedLock LOCK_B = new NamedLock("B");

    static final Thread LOCKING_AB = new Thread(() -> lockFirstThenSecond(LOCK_A, LOCK_B));
    static final Thread LOCKING_BA = new Thread(() -> lockFirstThenSecond(LOCK_B, LOCK_A));

    @Test
    public void testDeadlock() throws InterruptedException {
        try {
            LOCKING_AB.start();
            LOCKING_BA.start();

            LOCKING_AB.join(5000);
            LOCKING_BA.join(5000);

            assertTrue(LOCKING_AB.isAlive());
            assertTrue(LOCKING_BA.isAlive());

        } finally {
            if (LOCKING_AB.isAlive()) {
                LOCKING_AB.interrupt();
            }
            if (LOCKING_BA.isAlive()) {
                LOCKING_BA.interrupt();
            }
        }
    }

    static void lockFirstThenSecond(final NamedLock lock1, final NamedLock lock2) {
        try {
            lock1.lock();
            printLock(lock1, "1st", true);

            Thread.sleep(1000);

            lock2.lock();
            printLock(lock2, "2nd", true);

            // The place of the code that needs both locks...

        } catch (final InterruptedException e) {
            // We cannot throw the checked InterruptedException from Runnable.run() because it is not declared.
            throw new RuntimeException(e);

        } finally {
            if (lock1.isLocked() && lock1.isHeldByCurrentThread()) {
                lock1.unlock();
            }
            if (lock2.isLocked() && lock2.isHeldByCurrentThread()) {
                lock2.unlock();
            }
        }
    }

    static class NamedLock extends ReentrantLock {

        String name;

        NamedLock(final String name) {
            this.name = name;
        }
    }

    static void printLock(NamedLock lock, String tryNr, boolean locked) {
        System.out.println("Lock: " + lock.name +
                ", tryNr: " + tryNr +
                ", tryLock: " + locked +
                ", isLocked: " + lock.isLocked() +
                ", isHeldByCurrentThread: " + lock.isHeldByCurrentThread() +
                ", getHoldCount: " + lock.getHoldCount());
    }
}