package pet.practice.java.concurrency.lowlevel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Deadlock {

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
    }

    static void lockFirstThenSecond(final Lock lock1, final Lock lock2) {
        try {
            synchronized (lock1) {
                printLock(lock1, "1st", true);
                Thread.sleep(1000);
                synchronized (lock2) {
                    printLock(lock2, "2nd", true);
                }
            }
        } catch (final InterruptedException e) {
            // We cannot throw the checked InterruptedException from Runnable.run() because it is not declared.
            throw new RuntimeException(e);
        }
    }

    static class Lock {

        String name;

        Lock(final String name) {
            this.name = name;
        }
    }

    static void printLock(Lock lock, String tryNr, boolean locked) {
        System.out.println("Lock: " + lock.name +
                ", tryNr: " + tryNr +
                ", tryLock: " + locked);
    }
}