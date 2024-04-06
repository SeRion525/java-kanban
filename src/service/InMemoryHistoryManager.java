package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>(10);
    }

    @Override
    public void addTask(Task task) {
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
