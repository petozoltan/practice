package pet.practice.java.concurrency.lowlevel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Deadlock {

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
            throw new RuntimeException(e);
        }
    }

    static class NamedLock {

        String name;

        NamedLock(final String name) {
            this.name = name;
        }
    }

    static void printLock(NamedLock lock, String tryNr, boolean locked) {
        System.out.println("Lock: " + lock.name +
                ", tryNr: " + tryNr +
                ", tryLock: " + locked);
    }
}