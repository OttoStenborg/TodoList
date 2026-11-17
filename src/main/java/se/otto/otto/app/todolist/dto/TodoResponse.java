package se.otto.otto.app.todolist.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import se.otto.otto.app.todolist.model.Priority;
import se.otto.otto.app.todolist.model.Todo;

public record TodoResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        Priority priority,
        LocalDate dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted(),
                todo.getPriority(),
                todo.getDueDate(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }
}
