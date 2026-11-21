package se.otto.otto.app.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import se.otto.otto.app.todolist.model.CalendarEvent;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

}
