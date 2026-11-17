package se.otto.otto.app.todolist.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import se.otto.otto.app.todolist.dto.TodoRequest;
import se.otto.otto.app.todolist.dto.TodoResponse;
import se.otto.otto.app.todolist.service.TodoService;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoResponse> getAll(@RequestParam(required = false) Boolean completed) {
        return todoService.findAll(completed);
    }

    @GetMapping("/{id}")
    public TodoResponse getById(@PathVariable Long id) {
        return todoService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse create(@Valid @RequestBody TodoRequest request) {
        return todoService.create(request);
    }

    @PutMapping("/{id}")
    public TodoResponse update(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        return todoService.update(id, request);
    }

    @PatchMapping("/{id}/toggle")
    public TodoResponse toggle(@PathVariable Long id) {
        return todoService.toggleCompleted(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        todoService.delete(id);
    }
}
