package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static util.TaskTestUtil.assertEqualsTask;


public abstract class HistoryManagerTest<T extends HistoryManager> {
    T history;

    protected abstract T createHistory();

    @BeforeEach
    void init() {
        history = createHistory();
    }

    @DisplayName("Добавить задачу в пустую историю")
    @Test
    void shouldAddTaskInEmptyHistory() {
        Task task = new Task("task1", "task1 desc", Status.NEW);
        history.add(task);

        assertFalse(history.getHistory().isEmpty(), "Задача не добавилась в историю");
    }

    @DisplayName("Добавить задачу в не пустую историю")
    @Test
    void shouldAddTaskInNotEmptyHistory() {
        Task task1 = new Task("task1", "task1 desc", Status.NEW);
        task1.setId(0);
        Task task2 = new Task("task2", "task2 desc", Status.NEW);
        task2.setId(1);
        history.add(task1);
        history.add(task2);

        assertEquals(2, history.getHistory().size(), "Вторая задача не добавилась в историю");
    }

    @DisplayName("Добавить задачу в историю без дублирования")
    @Test
    void shouldAddTaskInHistoryWithoutDuplication() {
        Task task1 = new Task("task1", "task1 desc", Status.NEW);
        history.add(task1);

        Task updatedTask1 = new Task("updatedTask1", "task1 desc", Status.NEW);
        history.add(updatedTask1);

        assertEquals(1, history.getHistory().size(), "Задача продублировалась");
        assertEqualsTask(updatedTask1, history.getHistory().getFirst(), "Задача не обновилась");
    }

    @DisplayName("Получить задачи в порядке добавления")
    @Test
    void shouldGetHistorySortedByAdding() {
        Task task1 = new Task("task1", "task1 desc", Status.NEW);
        task1.setId(0);
        Task task2 = new Task("task2", "task2 desc", Status.NEW);
        task2.setId(1);
        Task task3 = new Task("task3", "task3 desc", Status.NEW);
        task3.setId(2);

        history.add(task1);
        history.add(task2);
        history.add(task3);

        assertEquals(task1, history.getHistory().get(0), "Задачи не в правильном порядке");
        assertEquals(task2, history.getHistory().get(1), "Задачи не в правильном порядке");
        assertEquals(task3, history.getHistory().get(2), "Задачи не в правильном порядке");
    }

    @DisplayName("Удалить задачу в начале истории")
    @Test
    void shouldRemoveTaskFromHeadOfHistory() {
        Task task1 = new Task("task1", "task1 desc", Status.NEW);
        task1.setId(0);
        Task task2 = new Task("task2", "task2 desc", Status.NEW);
        task2.setId(1);
        Task task3 = new Task("task3", "task3 desc", Status.NEW);
        task3.setId(2);

        history.add(task1);
        history.add(task2);
        history.add(task3);

        history.remove(task1.getId());

        assertEquals(2, history.getHistory().size(), "Задача не удалилась");
    }

    @DisplayName("Удалить задачу в середине истории")
    @Test
    void shouldRemoveTaskFromMiddleOfHistory() {
        Task task1 = new Task("task1", "task1 desc", Status.NEW);
        task1.setId(0);
        Task task2 = new Task("task2", "task2 desc", Status.NEW);
        task2.setId(1);
        Task task3 = new Task("task3", "task3 desc", Status.NEW);
        task3.setId(2);

        history.add(task1);
        history.add(task2);
        history.add(task3);

        history.remove(task2.getId());

        assertEquals(2, history.getHistory().size(), "Задача не удалилась");
    }

    @DisplayName("Удалить задачу в конце истории")
    @Test
    void shouldRemoveTaskFromTailOfHistory() {
        Task task1 = new Task("task1", "task1 desc", Status.NEW);
        task1.setId(0);
        Task task2 = new Task("task2", "task2 desc", Status.NEW);
        task2.setId(1);
        Task task3 = new Task("task3", "task3 desc", Status.NEW);
        task3.setId(2);

        history.add(task1);
        history.add(task2);
        history.add(task3);

        history.remove(task3.getId());

        assertEquals(2, history.getHistory().size(), "Задача не удалилась");
    }

    @DisplayName("Удалить задачу из пустой истории")
    @Test
    void shouldRemoveTaskFromEmptyHistory() {
        assertDoesNotThrow(() -> history.remove(0));
    }
}
