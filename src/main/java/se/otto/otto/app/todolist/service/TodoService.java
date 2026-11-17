package se.otto.otto.app.todolist.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.otto.otto.app.todolist.dto.TodoRequest;
import se.otto.otto.app.todolist.dto.TodoResponse;
import se.otto.otto.app.todolist.exception.TodoNotFoundException;
import se.otto.otto.app.todolist.model.Todo;
import se.otto.otto.app.todolist.repository.TodoRepository;

@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> findAll(Boolean completed) {
        List<Todo> todos = completed == null
                ? todoRepository.findAll()
                : todoRepository.findByCompleted(completed);

        return todos.stream()
                .map(TodoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TodoResponse findById(Long id) {
        return TodoResponse.from(getTodo(id));
    }

    public TodoResponse create(TodoRequest request) {
        Todo todo = new Todo(
                request.title(),
                request.description(),
                request.priority(),
                request.dueDate()
        );
        return TodoResponse.from(todoRepository.save(todo));
    }

    public TodoResponse update(Long id, TodoRequest request) {
        Todo todo = getTodo(id);
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setPriority(request.priority());
        todo.setDueDate(request.dueDate());
        return TodoResponse.from(todo);
    }

    public TodoResponse toggleCompleted(Long id) {
        Todo todo = getTodo(id);
        todo.setCompleted(!todo.isCompleted());
        return TodoResponse.from(todo);
    }

    public void delete(Long id) {
        todoRepository.delete(getTodo(id));
    }

    private Todo getTodo(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }
}
