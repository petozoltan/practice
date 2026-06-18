package pet.practice.java.object;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HackedMapTest {

    @Test
    public void testHashMap_Normal() {

        class RegularKey {

            RegularKey(final int doesntMatter, final int whatever) {
            }
        }

        final Map<RegularKey, String> map = new HashMap<>();

        map.put(new RegularKey(1, 1), "a11"); // New
        map.put(new RegularKey(1, 2), "a12"); // New because of different equals & hashCode (different identity)
        map.put(new RegularKey(2, 1), "a21"); // New because of different equals & hashCode (different identity)
        map.put(new RegularKey(2, 2), "a22"); // New because of different equals & hashCode (different identity)
        map.put(new RegularKey(2, 2), "b22"); // New because of different equals & hashCode (different identity)

        assertEquals(5, map.size());
    }

    @Test
    public void testHashMap_Hacked() {

        class AdjustableKey {

            final int equals;
            final int hashCode;

            public AdjustableKey(final int equals, final int hashCode) {
                this.equals = equals;
                this.hashCode = hashCode;
            }

            @Override
            public int hashCode() {
                return hashCode;
            }

            @Override
            public boolean equals(final Object obj) {
                return equals == ((AdjustableKey) obj).equals;
            }
        }

        final Map<AdjustableKey, String> map = new HashMap<>();

        map.put(new AdjustableKey(1, 1), "a11"); // New
        map.put(new AdjustableKey(1, 2), "a12"); // New because of different hashCode
        map.put(new AdjustableKey(2, 1), "a21"); // New because of different equals
        map.put(new AdjustableKey(2, 2), "a22"); // New because of different hashCode
        map.put(new AdjustableKey(2, 2), "b22"); // Replaces a22

        assertEquals(4, map.size());
    }

    @Test
    public void testHashMap_Records() {

        record AdjustableKey(int equals, int hashcode) {
        }

        final Map<AdjustableKey, String> map = new HashMap<>();

        map.put(new AdjustableKey(1, 1), "a11"); // New
        map.put(new AdjustableKey(1, 2), "a12"); // New because of different hashCode
        map.put(new AdjustableKey(2, 1), "a21"); // New because of different equals
        map.put(new AdjustableKey(2, 2), "a22"); // New because of different hashCode
        map.put(new AdjustableKey(2, 2), "b22"); // Replaces a22

        assertEquals(4, map.size());
    }
}
