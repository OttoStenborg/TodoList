package se.otto.otto.app.todolist.exception;

public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(Long id) {
        super("Hittade ingen uppgift med id " + id);
    }
}
