package se.otto.otto.app.todolist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import se.otto.otto.app.todolist.model.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByCompleted(boolean completed);
}
