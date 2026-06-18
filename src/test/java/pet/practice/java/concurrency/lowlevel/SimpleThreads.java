package pet.practice.java.concurrency.lowlevel;

import static java.lang.System.err;
import static java.lang.System.out;

public class SimpleThreads {

    // ————————————————————————————————————————
    // Main thread
    // ————————————————————————————————————————

    /// Milliseconds, the main thread will wait for the other thread to finish before it interrupts it.
    /// * large value to see the other thread finish normally (it prints all the lines of the poem).
    /// * small value (< 4 * 1000) to interrupt the other thread before it finishes.
    final long TEST_TIMEOUT_MS = 9000; // * 60 * 60;

    void main() throws InterruptedException {

        out.println(createThreadMessage("Starting " + NAME + " thread..."));
        long startTime = System.currentTimeMillis();
        Thread t = new Thread(this::printPoem, NAME);
        t.start();

        while (t.isAlive()) {
            System.out.println(createThreadMessage("Waiting for " + NAME + " thread to finish..."));

            t.join(1000);
            if (((System.currentTimeMillis() - startTime) > TEST_TIMEOUT_MS) && t.isAlive()) {
                out.println(createThreadMessage("Interrupting " + NAME + " thread..."));
                t.interrupt();
                t.join(); // or t.join(0) = Wait forever
            }
        }
        out.println(createThreadMessage("Thread finished"));
    }

    // ————————————————————————————————————————
    // Poem thread
    // ————————————————————————————————————————

    static final String NAME = "Poem";

    static final String[] POEM = {
            "Mares eat oats",
            "Does eat oats",
            "Little lambs eat ivy",
            "A kid will eat ivy too"
    };

    void printPoem() {
        try {
            for (final String line : POEM) {
                Thread.sleep(4000);
                err.println(createThreadMessage(line));
            }
        } catch (InterruptedException e) {
            err.println(createThreadMessage("Thread interrupted."));
        }
    }

    String createThreadMessage(String message) {
        return String.format("[%s]: %s", Thread.currentThread().getName(), message);
    }
}