package se.otto.otto.app.todolist.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class CalendarEventTest {

    @Test
    void constructorSetsAndScheduleTimeUpdatesStartAndEnd() {
        LocalDateTime initialStart = LocalDateTime.of(2026, 11, 21, 10, 0);
        LocalDateTime initialEnd = LocalDateTime.of(2026, 11, 21, 11, 0);
        CalendarEvent event = new CalendarEvent("uid", "title", initialStart, initialEnd, false, null, null);

        assertEquals(initialStart, event.getStart());
        assertEquals(initialEnd, event.getEnd());

        LocalDateTime newStart = LocalDateTime.of(2026, 11, 22, 12, 0);
        LocalDateTime newEnd = LocalDateTime.of(2026, 11, 22, 13, 30);
        event.scheduleTime(newStart, newEnd);

        assertEquals(newStart, event.getStart());
        assertEquals(newEnd, event.getEnd());
    }

    @Test
    void scheduleTimeRejectsStartAfterEndWithoutChangingExistingTimes() {
        LocalDateTime initialStart = LocalDateTime.of(2026, 11, 21, 10, 0);
        LocalDateTime initialEnd = LocalDateTime.of(2026, 11, 21, 11, 0);
        CalendarEvent event = new CalendarEvent("uid", "title", initialStart, initialEnd, false, null, null);

        assertThrows(IllegalArgumentException.class, () -> event.scheduleTime(initialEnd, initialStart));

        assertEquals(initialStart, event.getStart());
        assertEquals(initialEnd, event.getEnd());
    }

    @Test
    void scheduleTimeRejectsNullTimes() {
        CalendarEvent event = new CalendarEvent(
                "uid",
                "title",
                LocalDateTime.of(2026, 11, 21, 10, 0),
                LocalDateTime.of(2026, 11, 21, 11, 0),
                false,
                null,
                null);

        assertThrows(NullPointerException.class, () -> event.scheduleTime(null, event.getEnd()));
    }

    @Test
    void constructorRejectsNullUidAndTitle() {
        LocalDateTime start = LocalDateTime.of(2026, 11, 21, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 11, 21, 11, 0);

        assertThrows(NullPointerException.class,
                () -> new CalendarEvent(null, "title", start, end, false, null, null));
        assertThrows(NullPointerException.class,
                () -> new CalendarEvent("uid", null, start, end, false, null, null));
    }

    @Test
    void setTitleRejectsNull() {
        CalendarEvent event = new CalendarEvent(
                "uid",
                "title",
                LocalDateTime.of(2026, 11, 21, 10, 0),
                LocalDateTime.of(2026, 11, 21, 11, 0),
                false,
                null,
                null);

        assertThrows(NullPointerException.class, () -> event.setTitle(null));
        assertEquals("title", event.getTitle());
    }
}
