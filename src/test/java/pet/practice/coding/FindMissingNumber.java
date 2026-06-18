package pet.practice.coding;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class FindMissingNumber {

    final int SMALLEST = 1;
    final int BIGGEST = 100;

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
        testFindMissingNumber(createRandomNumber());
    }

    @Test
    void testFindMissingNumber_Smallest() {
        testFindMissingNumber(SMALLEST);
    }

    @Test
    void testFindMissingNumber_Largest() {
        testFindMissingNumber(BIGGEST);
    }

    // ————————————————————————————————————————
    // Test helpers
    // ————————————————————————————————————————

    List<Integer> createList() {
        List<Integer> numbers = new ArrayList<>(IntStream.rangeClosed(SMALLEST, BIGGEST).boxed().toList());
        assumeTrue(numbers.size() == BIGGEST - SMALLEST + 1);
        Collections.shuffle(numbers);
        return numbers;
    }

    int createRandomNumber() {
        final int randomNumber = new Random().nextInt(BIGGEST - SMALLEST + 1) + SMALLEST;
        assumeTrue(SMALLEST <= randomNumber && randomNumber <= BIGGEST);
        return randomNumber;
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
