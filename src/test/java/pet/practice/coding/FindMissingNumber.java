package pet.practice.coding;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class FindMissingNumber {

    static final int SMALLEST = 1;
    static final int LARGEST = 100;

    static final Random RANDOM = new Random();

    // ————————————————————————————————————————
    // Solutions
    // ————————————————————————————————————————

    int findMissingNumber_WrittenOnTheInterview(List<Integer> numbers) {
        Collections.sort(numbers); // A copy would be nicer, not changing the input
        int previous = 0;
        for (Integer number : numbers) {
            int current = number.intValue();
            if (current - previous > 1) {
                return previous + 1;
            }
            previous = current;
        }
        return 100;
    }

    int findMissingNumber_CorrectedLater(List<Integer> numbers) {
        Collections.sort(new ArrayList<>(numbers));
        int expected = 1;
        for (int current : numbers) {
            if (current != expected) {
                return expected;
            }
            expected = current + 1;
        }
        return expected;
    }

    int findMissingNumber_GeneratedByAI(List<Integer> numbers) {
        int n = numbers.size() + 1; // Since one number is missing
        int expectedSum = n * (n + 1) / 2; // Sum of first n natural numbers
        int actualSum = numbers.stream().mapToInt(Integer::intValue).sum(); // Sum of given numbers
        return expectedSum - actualSum; // The missing number
    }

    // ————————————————————————————————————————
    // Tests
    // ————————————————————————————————————————

    private void testFindMissingNumber(final int missingNumber) {
        List<Integer> numbers = createList();
        removeNumber(numbers, missingNumber);
        assertEquals(missingNumber, findMissingNumber_WrittenOnTheInterview(numbers));
        assertEquals(missingNumber, findMissingNumber_CorrectedLater(numbers));
        assertEquals(missingNumber, findMissingNumber_GeneratedByAI(numbers));
    }

    @Test
    void testFindMissingNumber_Random() {
        testFindMissingNumber(createRandomIntInRange(SMALLEST, LARGEST));
    }

    @Test
    void testFindMissingNumber_Smallest() {
        testFindMissingNumber(SMALLEST);
    }

    @Test
    void testFindMissingNumber_Largest() {
        testFindMissingNumber(LARGEST);
    }

    // ————————————————————————————————————————
    // Test helpers
    // ————————————————————————————————————————

    List<Integer> createList() {
        List<Integer> numbers = new ArrayList<>(IntStream.rangeClosed(SMALLEST, LARGEST).boxed().toList());
        assumeTrue(numbers.size() == LARGEST - SMALLEST + 1);
        Collections.shuffle(numbers);
        return numbers;
    }

    /// `Random.nextInt(int bound)` generates between 0 (inclusive) and bound (exclusive).
    static int createRandomIntInRange(int smallest, int largest) {
        final int randomInt = RANDOM.nextInt(largest - smallest + 1) + smallest;
        assumeTrue(SMALLEST <= randomInt && randomInt <= LARGEST);
        return randomInt;
    }

    void removeNumber(final List<Integer> numbers, final int number) {
        final int sizeBefore = numbers.size();
        final Iterator<Integer> iterator = numbers.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().intValue() == number) {
                iterator.remove();
                break;
            }
        }
        assumeTrue(numbers.size() == sizeBefore - 1);
    }
}
