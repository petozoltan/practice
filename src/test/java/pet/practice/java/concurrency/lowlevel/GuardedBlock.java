package pet.practice.java.concurrency.lowlevel;

import java.util.Random;

public class GuardedBlock {

    static final String[] POEM = {
            "Mares eat oats",
            "Does eat oats",
            "Little lambs eat ivy",
            "A kid will eat ivy too"
    };

    static final String DONE = "DONE";
    static final int WAIT_MAX = 5000;

    static final MessageQueue MESSAGE_QUEUE = new MessageQueue();
    static final Object LOCK = new Object();
    static final Random RANDOM = new Random();

    static final Thread PRODUCER = new Thread(GuardedBlock::produceMessage);
    static final Thread CONSUMER = new Thread(GuardedBlock::consumeMessage);

    // ————————————————————————————————————————
    // Producer / Consumer
    // ————————————————————————————————————————

    static void main() {
        PRODUCER.start();
        CONSUMER.start();
    }

    static void produceMessage() {
        for (final String s : POEM) {
            MESSAGE_QUEUE.put(s);
            sleepRandom();
        }
        MESSAGE_QUEUE.put(DONE);
    }

    static void consumeMessage() {
        for (String message = MESSAGE_QUEUE.pull(); !message.equals(DONE); message = MESSAGE_QUEUE.pull()) {
            System.out.println(message);
            sleepRandom();
        }
    }

    private static void sleepRandom() {
        try {
            Thread.sleep(RANDOM.nextInt(WAIT_MAX));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // ————————————————————————————————————————
    // Queue
    // ————————————————————————————————————————

    static class MessageQueue {

        String message = null;

        void put(String message) {
            synchronized (LOCK) {
                while (this.message != null) {
                    try {
                        // 1. Current thread releases lock.
                        // 2. Current thread waits.
                        // Can be called only on the acquired lock.
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                this.message = message;
                LOCK.notifyAll(); // Can be called only on the acquired lock.
            }
        }

        String pull() {
            synchronized (LOCK) {
                while (message == null) {
                    try {
                        // 1. Current thread releases lock.
                        // 2. Current thread waits.
                        // Can be called only on the acquired lock.
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                String processedMessage = message;
                message = null;
                LOCK.notifyAll(); // Can be called only on the acquired lock.
                return processedMessage;
            }
        }
    }
}