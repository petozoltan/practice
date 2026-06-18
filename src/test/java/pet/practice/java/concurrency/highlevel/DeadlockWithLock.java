package pet.practice.java.concurrency.highlevel;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeadlockWithLock {

    static final Lock LOCK_A = new Lock("A");
    static final Lock LOCK_B = new Lock("B");

    static final Thread LOCKING_AB = new Thread(() -> lockFirstThenSecond(LOCK_A, LOCK_B));
    static final Thread LOCKING_BA = new Thread(() -> lockFirstThenSecond(LOCK_B, LOCK_A));

    @Test
    public void testDeadlock() throws InterruptedException {

        LOCKING_AB.start();
        LOCKING_BA.start();

        LOCKING_AB.join(5000);
        LOCKING_BA.join(5000);

        assertTrue(LOCKING_AB.isAlive());
        assertTrue(LOCKING_BA.isAlive());

        if (LOCKING_AB.isAlive()) {
            LOCKING_AB.interrupt();
        }
        if (LOCKING_BA.isAlive()) {
            LOCKING_BA.interrupt();
        }
    }

    static void lockFirstThenSecond(final Lock lock1, final Lock lock2) {
        try {
            lock1.lock();
            printLock(lock1, "1st", true);

            Thread.sleep(1000);

            lock2.lock();
            printLock(lock2, "2nd", true);

        } catch (final InterruptedException e) {
            // We cannot throw the checked InterruptedException from Runnable.run() because it is not declared.
            throw new RuntimeException(e);

        } finally {
            lock1.unlock();
            lock2.unlock();
        }
    }

    static class Lock extends ReentrantLock {

        String name;

        Lock(final String name) {
            this.name = name;
        }
    }

    static void printLock(Lock lock, String tryNr, boolean locked) {
        System.out.println("Lock: " + lock.name +
                ", tryNr: " + tryNr +
                ", tryLock: " + locked +
                ", isLocked: " + lock.isLocked() +
                ", isHeldByCurrentThread: " + lock.isHeldByCurrentThread() +
                ", getHoldCount: " + lock.getHoldCount());
    }
}