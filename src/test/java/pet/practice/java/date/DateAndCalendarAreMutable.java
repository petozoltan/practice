package pet.practice.java.date;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DateAndCalendarAreMutable {

    // ————————————————————————————————————————
    // Mutable
    // ————————————————————————————————————————

    static class MutableClass {

        private final Date date;
        private final Calendar calendar;

        public static MutableClass getInstance() {
            return new MutableClass(new Date(), Calendar.getInstance());
        }

        public MutableClass(Date date, Calendar calendar) {
            this.date = date;
            this.calendar = calendar;
        }

        public Date getDate() {
            return date; // Member returned
        }

        public Calendar getCalendar() {
            return calendar; // Member returned
        }
    }

    @Test
    public void testDateAndCalendarAreModifiable() {

        MutableClass instance = MutableClass.getInstance();

        String dateBefore = instance.getDate().toString();
        instance.getDate().setYear(instance.getDate().getYear() + 1);
        assertNotEquals(dateBefore, instance.getDate().toString()); // Modified

        String calendarBefore = instance.getCalendar().toString();
        instance.getCalendar().set(Calendar.YEAR, instance.getCalendar().get(Calendar.YEAR) + 1);
        assertNotEquals(calendarBefore, instance.getCalendar().toString()); // Modified
    }

    // ————————————————————————————————————————
    // Immutable
    // ————————————————————————————————————————

    static class ImmutableClass {

        private final Date date;
        private final Calendar calendar;

        public static ImmutableClass getInstance() {
            return new ImmutableClass(new Date(), Calendar.getInstance());
        }

        public ImmutableClass(Date date, Calendar calendar) {
            this.date = date;
            this.calendar = calendar;
        }

        public Date getDate() {
            return (Date) date.clone(); // Copy returned
        }

        public Calendar getCalendar() {
            return (Calendar) calendar.clone(); // Copy returned
        }
    }

    @Test
    public void testDateAndCalendar_PreventModification() {

        ImmutableClass instance = ImmutableClass.getInstance();

        String dateBefore = instance.getDate().toString();
        instance.getDate().setYear(instance.getDate().getYear() + 1);
        assertEquals(dateBefore, instance.getDate().toString()); // Not modified

        String calendarBefore = instance.getCalendar().toString();
        instance.getCalendar().set(Calendar.YEAR, instance.getCalendar().get(Calendar.YEAR) + 1);
        assertEquals(calendarBefore, instance.getCalendar().toString()); // Not modified
    }

}
