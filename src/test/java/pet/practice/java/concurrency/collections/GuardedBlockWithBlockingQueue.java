package pet.practice.java.concurrency.collections;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GuardedBlockWithBlockingQueue {

    static final String[] POEM = {
            "Mares eat oats",
            "Does eat oats",
            "Little lambs eat ivy",
            "A kid will eat ivy too"
    };

    static final String DONE = "DONE";
    static final int WAIT_MAX = 5000;

    /// The guarded blocks are implemented in the BlockingQueue implementation.
    static final BlockingQueue<String> MESSAGE_QUEUE = new LinkedBlockingQueue<>();

    static final Random RANDOM = new Random();

    static final Thread PRODUCER = new Thread(GuardedBlockWithBlockingQueue::produceMessage);
    static final Thread CONSUMER = new Thread(GuardedBlockWithBlockingQueue::consumeMessage);

    // ————————————————————————————————————————
    // Producer / Consumer
    // ————————————————————————————————————————

    static void main() {
        PRODUCER.start();
        CONSUMER.start();
    }

    static void produceMessage() {
        try {
            for (final String s : POEM) {
                MESSAGE_QUEUE.put(s);
                sleepRandom();
            }
            MESSAGE_QUEUE.put(DONE);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static void consumeMessage() {
        try {
            for (String message = MESSAGE_QUEUE.take(); !message.equals(DONE); message = MESSAGE_QUEUE.take()) {
                System.out.println(message);
                sleepRandom();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sleepRandom() {
        try {
            Thread.sleep(RANDOM.nextInt(WAIT_MAX));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}