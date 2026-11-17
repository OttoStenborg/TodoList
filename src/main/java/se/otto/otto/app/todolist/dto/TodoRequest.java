package se.otto.otto.app.todolist.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import se.otto.otto.app.todolist.model.Priority;

public record TodoRequest(
        @NotBlank(message = "Titel får inte vara tom")
        @Size(max = 255)
        String title,

        @Size(max = 1000)
        String description,

        Priority priority,

        LocalDate dueDate
) {
}
