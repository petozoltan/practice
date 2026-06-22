package pet.practice.java.concurrency.highlevel;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DeadlockWithLockSolved {

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

            assertFalse(LOCKING_AB.isAlive());
            assertFalse(LOCKING_BA.isAlive());

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
            // We cannot throw the checked InterruptedException from Runnable.run() because it is not declared.
            throw new RuntimeException(e);

        } finally {
            if (locked1) {
                lock1.unlock();
            }
            if (locked2) {
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