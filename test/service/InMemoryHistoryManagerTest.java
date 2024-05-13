package service;

import model.EpicTask;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Managers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private List<Task> tasks;
    private Task task1;
    private Task task2;
    private EpicTask epicTask1;
    private EpicTask epicTask2;
    private SubTask subTask1InEpicTask1;
    private SubTask subTask2InEpicTask1;
    private SubTask subTask3InEpicTask2;
    private SubTask subTask4InEpicTask2;
    private SubTask subTask5InEpicTask2;
    private SubTask subTask6InEpicTask2;


    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        tasks = new ArrayList<>();

        task1 = new Task("task1", "task2 desc", Status.NEW);
        task2 = new Task("task2", "task2 desc", Status.NEW);
        epicTask1 = new EpicTask("epicTask1", "epicTask1 desc");
        epicTask2 = new EpicTask("epicTask2", "epicTask2 desc");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(epicTask1);
        taskManager.createTask(epicTask2);

        subTask1InEpicTask1 = new SubTask("subTask1InEpicTask1", "subTask1InEpicTask1 disc",
                Status.NEW, 2);
        subTask2InEpicTask1 = new SubTask("subTask2InEpicTask1", "subTask2InEpicTask1 disc",
                Status.NEW, 2);
        subTask3InEpicTask2 = new SubTask("subTask3InEpicTask2", "subTask3InEpicTask2 disc",
                Status.NEW, 3);
        subTask4InEpicTask2 = new SubTask("subTask4InEpicTask2", "subTask4InEpicTask2 disc",
                Status.NEW, 3);
        subTask5InEpicTask2 = new SubTask("subTask5InEpicTask2", "subTask5InEpicTask2 disc",
                Status.NEW, 3);
        subTask6InEpicTask2 = new SubTask("subTask6InEpicTask2", "subTask6InEpicTask2 disc",
                Status.NEW, 3);

        taskManager.createTask(subTask1InEpicTask1);
        taskManager.createTask(subTask2InEpicTask1);
        taskManager.createTask(subTask3InEpicTask2);
        taskManager.createTask(subTask4InEpicTask2);
        taskManager.createTask(subTask5InEpicTask2);
        taskManager.createTask(subTask6InEpicTask2);

        for (int i = 0; i < 2; i++) {
            tasks.add(taskManager.getTask(i));
        }

        for (int i = 2; i < 4; i++) {
            tasks.add(taskManager.getEpicTask(i));
        }

        for (int i = 4; i < 10; i++) {
            tasks.add(taskManager.getSubTask(i));
        }
    }

    @Test
    void getHistory() {
        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История не возвращается.");
        assertEquals(10, history.size(), "Не все задачи добавились в историю.");
    }

    @Test
    void shouldNotSameTasksInHistory() {
        Task task = taskManager.getTask(0);
        List<Task> history = taskManager.getHistory();

        int currTaskCount = 0;
        for (Task curr : history) {
            if (curr.equals(task)) {
                currTaskCount++;
            }
        }

        assertEquals(1, currTaskCount, "Задача в истории повторяется");
    }

//    @Test
//    void addNewTaskWhenHistoryIsFull() {
//        List<Task> previousHistory = taskManager.getHistory();
//
//        Task gottenTask = taskManager.getTask(0);
//        List<Task> updatedHistory = taskManager.getHistory();
//
//        assertNotEquals(previousHistory, updatedHistory, "История не обновилась.");
//        assertEquals(gottenTask, updatedHistory.get(9), "Новая задача не добавилась в конец списка.");
//    }

    @Test
    void shouldRemoveTaskFromHistory() {
        taskManager.removeTask(0);

        List<Task> history = taskManager.getHistory();

        assertFalse(history.contains(task1), "Задача не удалилась из истории");
    }

    @Test
    void shouldRemoveAllSubTasksFromHistoryWhenEpicTaskRemoved() {
        taskManager.removeTask(2);

        List<Task> history = taskManager.getHistory();

        assertFalse(history.contains(subTask1InEpicTask1), "Подзадача 1 не удалилась из истории");
        assertFalse(history.contains(subTask2InEpicTask1), "Подзадача 2 не удалилась из истории");
    }



    @Test
    void shouldWasPreviousVersionOfTaskInHistory() {
        Task newTask1 = new Task("task10", "task10 desc", Status.IN_PROGRESS);
        newTask1.setId(0);

        taskManager.updateTask(newTask1);

        List<Task> history = taskManager.getHistory();
        Task previousVersionOfTask1 = tasks.get(0);
        Task task1FromHistory = history.get(0);

        assertEquals(previousVersionOfTask1.getId(), task1FromHistory.getId(),
                "У задач разный ID");
        assertEquals(previousVersionOfTask1.getTitle(), task1FromHistory.getTitle(),
                "У задачи разный заголовок");
        assertEquals(previousVersionOfTask1.getDescription(), task1FromHistory.getDescription(),
                "У задач разное описание");
        assertEquals(previousVersionOfTask1.getStatus(), task1FromHistory.getStatus(),
                "У задач разный статус");
    }

    @Test
    void shouldWasNewVersionOfTaskInHistoryWhenTaskAddedInHistory() {
        Task newTask1 = new Task("task10", "task10 desc", Status.IN_PROGRESS);
        newTask1.setId(0);

        taskManager.updateTask(newTask1);
        taskManager.getTask(0);

        List<Task> history = taskManager.getHistory();
        Task task1FromHistory = history.get(0);

        assertEquals(newTask1.getId(), task1FromHistory.getId(),
                "У задач разный ID");
        assertEquals(newTask1.getTitle(), task1FromHistory.getTitle(),
                "У задачи разный заголовок");
        assertEquals(newTask1.getDescription(), task1FromHistory.getDescription(),
                "У задач разное описание");
        assertEquals(newTask1.getStatus(), task1FromHistory.getStatus(),
                "У задач разный статус");
    }
}