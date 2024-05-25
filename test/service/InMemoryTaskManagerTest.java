package service;

import model.EpicTask;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager = Managers.getDefault();
    private Task task1;
    private Task task2;
    private EpicTask epicTask1;
    private EpicTask epicTask2;
    private SubTask subTask1;
    private SubTask subTask2;
    private SubTask subTask3;

    @BeforeEach
    void beforeEach() {
        task1 = new Task("Task1", "Task1 description", Status.NEW);
        task2 = new Task("Task2", "Task2 description", Status.NEW);
        epicTask1 = new EpicTask("EpicTask1", "EpicTask1 description");
        epicTask2 = new EpicTask("EpicTask2", "EpicTask2 description");
        subTask1 = new SubTask("SubTask1", "SubTask1 description", Status.NEW, epicTask1.getId());
        subTask2 = new SubTask("SubTask2", "SubTask2 description", Status.NEW, epicTask1.getId());
        subTask3 = new SubTask("SubTask3", "SubTask3 description", Status.NEW, epicTask2.getId());
    }

    @Test
    void createNewTask() {
        taskManager.createTask(task1);

        Task createdTask = taskManager.getTask(task1.getId());

        assertNotNull(createdTask, "Задача не найдена.");
        assertEquals(task1, createdTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное кол-во задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void createNewEpicTask() {
        taskManager.createEpicTask(epicTask1);

        EpicTask createdEpicTask = taskManager.getEpicTask(epicTask1.getId());

        assertNotNull(createdEpicTask, "Эпик не найден.");
        assertEquals(epicTask1, createdEpicTask, "Эпики не совпадают.");

        List<EpicTask> EpicTasks = taskManager.getAllEpicTasks();

        assertNotNull(EpicTasks, "Эпики не возвращаются");
        assertEquals(1, EpicTasks.size(), "Неверное кол-во эпиков.");
        assertEquals(epicTask1, EpicTasks.get(0), "Эпики не совпадают");
    }

    @Test
    void createNewSubTask() {
        taskManager.createEpicTask(epicTask1);
        taskManager.createSubTask(subTask1);

        SubTask createdSubTask = taskManager.getSubTask(subTask1.getId());

        assertNotNull(createdSubTask, "Подзадача не найдена.");
        assertEquals(subTask1, createdSubTask, "Подзадачи не совпадают.");

        List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Подзадачи не возвращаются");
        assertEquals(1, subTasks.size(), "Неверное кол-во подзадач.");
        assertEquals(subTask1, subTasks.get(0), "Подзадачи не совпадают");

        List<SubTask> subTasksInEpicTask = taskManager.getSubTasksFromEpicTaskId(epicTask1.getId());

        assertNotNull(subTasksInEpicTask, "Задачи из эпика не возвращаются");
        assertEquals(1, subTasksInEpicTask.size(), "Неверное кол-во подзадач у эпика.");
        assertEquals(subTask1, subTasksInEpicTask.get(0), "Подзадача не совпадает с подзадачей из эпика");
    }

    @Test
    void shouldEqualsWhenTasksHasSameId() {
        taskManager.createTask(task1);
        Task sameTask1 = new Task("Task1", "Task1 description", Status.NEW);
        sameTask1.setId(0);

        Task savedTask1 = taskManager.getTask(task1.getId());

        assertEquals(sameTask1.getId(), savedTask1.getId(), "У задач разный ID");
        assertEquals(sameTask1.getTitle(), savedTask1.getTitle(), "У задач разный заголовок");
        assertEquals(sameTask1.getDescription(), savedTask1.getDescription(), "У задач разное описание");
        assertEquals(sameTask1.getStatus(), savedTask1.getStatus(), "У задач разный статус");
    }

    @Test
    void shouldEqualsWhenEpicTasksHasSameId() {
        taskManager.createEpicTask(epicTask1);
        taskManager.createSubTask(subTask1);

        EpicTask sameEpicTask1 = new EpicTask("EpicTask1", "EpicTask1 description");
        sameEpicTask1.setId(0);
        sameEpicTask1.addSubTaskId(1);

        EpicTask savedEpicTask1 = taskManager.getEpicTask(epicTask1.getId());

        assertEquals(sameEpicTask1.getId(), savedEpicTask1.getId(),
                "У эпиков разный ID");
        assertEquals(sameEpicTask1.getTitle(), savedEpicTask1.getTitle(),
                "У эпиков разный заголовок");
        assertEquals(sameEpicTask1.getDescription(), savedEpicTask1.getDescription(),
                "У эпиков разное описание");
        assertEquals(sameEpicTask1.getStatus(), savedEpicTask1.getStatus(),
                "У эпиков разный статус");
        assertEquals(sameEpicTask1.getSubTasksId(), savedEpicTask1.getSubTasksId(),
                "У эпиков разные id подзадач");
    }

    @Test
    void shouldEqualsWhenSubTasksHasSameId() {
        taskManager.createEpicTask(epicTask1);
        taskManager.createSubTask(subTask1);

        SubTask sameSubTask1 = new SubTask("SubTask1", "SubTask1 description",
                Status.NEW, epicTask1.getId());
        sameSubTask1.setId(1);

        SubTask savedSubTask1 = taskManager.getSubTask(subTask1.getId());

        assertEquals(sameSubTask1.getId(), savedSubTask1.getId(),
                "У подзадач разный ID");
        assertEquals(sameSubTask1.getTitle(), savedSubTask1.getTitle(),
                "У подзадач разный заголовок");
        assertEquals(sameSubTask1.getDescription(), savedSubTask1.getDescription(),
                "У подзадач разное описание");
        assertEquals(sameSubTask1.getStatus(), savedSubTask1.getStatus(),
                "У подзадач разный статус");
        assertEquals(sameSubTask1.getEpicTaskId(), savedSubTask1.getEpicTaskId(),
                "У подзадач разные id эпика");
    }

    @Test
    void updateTask() {
        taskManager.createTask(task1);
        Task taskWithNewData = new Task("Task1 new title", "Task1 new description", Status.IN_PROGRESS);
        taskWithNewData.setId(0);

        taskManager.updateTask(taskWithNewData);
        Task updatedTask = taskManager.getTask(0);

        assertEquals(taskWithNewData.getId(), updatedTask.getId(), "У задач разный ID");
        assertEquals(taskWithNewData.getTitle(), updatedTask.getTitle(), "У задачи разный заголовок");
        assertEquals(taskWithNewData.getDescription(), updatedTask.getDescription(), "У задач разное описание");
        assertEquals(taskWithNewData.getStatus(), updatedTask.getStatus(), "У задач разный статус");
    }

    @Test
    void updateSubTask() {
        taskManager.createEpicTask(epicTask1);
        taskManager.createSubTask(subTask1);

        SubTask subTask1WithNewData = new SubTask("SubTask1 new title", "SubTask1 new description",
                Status.IN_PROGRESS, epicTask1.getId());
        subTask1WithNewData.setId(1);

        taskManager.updateSubTask(subTask1WithNewData);
        SubTask updatedSubTask1 = taskManager.getSubTask(1);

        assertEquals(subTask1WithNewData.getId(), updatedSubTask1.getId(),
                "У подзадач разный ID");
        assertEquals(subTask1WithNewData.getTitle(), updatedSubTask1.getTitle(),
                "У подзадач разный заголовок");
        assertEquals(subTask1WithNewData.getDescription(), updatedSubTask1.getDescription(),
                "У подзадач разное описание");
        assertEquals(subTask1WithNewData.getStatus(), updatedSubTask1.getStatus(),
                "У подзадач разный статус");
        assertEquals(subTask1WithNewData.getEpicTaskId(), updatedSubTask1.getEpicTaskId(),
                "У подзадач разные id эпика");

        Status updatedEpicTaskStatus = taskManager.getEpicTask(0).getStatus();

        assertEquals(Status.IN_PROGRESS, updatedEpicTaskStatus, "Статус эпика не изменился на IN_PROGRESS.");
    }

    @Test
    void updateEpicTaskStatus() {
        taskManager.createEpicTask(epicTask1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        EpicTask createdEpicTask = taskManager.getEpicTask(0);
        assertEquals(Status.NEW, createdEpicTask.getStatus(), "Статус эпика должен быть NEW");

        SubTask newSubTask1 = new SubTask("SubTask1", "SubTask1 description",
                Status.IN_PROGRESS, epicTask1.getId());
        newSubTask1.setId(1);
        SubTask newSubTask2 = new SubTask("SubTask1", "SubTask1 description",
                Status.DONE, epicTask1.getId());
        newSubTask2.setId(2);

        taskManager.updateSubTask(newSubTask1);
        taskManager.updateSubTask(newSubTask2);

        createdEpicTask = taskManager.getEpicTask(0);
        assertEquals(Status.IN_PROGRESS, createdEpicTask.getStatus(), "Статус эпика должен быть IN_PROGRESS");

        newSubTask1 = new SubTask("SubTask1", "SubTask1 description",
                Status.DONE, epicTask1.getId());
        newSubTask1.setId(1);
        taskManager.updateSubTask(newSubTask1);

        createdEpicTask = taskManager.getEpicTask(0);
        assertEquals(Status.DONE, createdEpicTask.getStatus(), "Статус эпика должен быть DONE");
    }

    @Test
    void removeTask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.removeTask(0);
        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(1, tasks.size(), "Задача не удалилась.");

        taskManager.removeAllTasks();
        tasks = taskManager.getAllTasks();
        assertEquals(0, tasks.size(), "Все задачи не удалились.");
    }

    @Test
    void removeSubTask() {
        taskManager.createEpicTask(epicTask1);

        subTask1.setStatus(Status.DONE);
        taskManager.createSubTask(subTask1);

        taskManager.createSubTask(subTask2);

        taskManager.removeTask(2);

        List<SubTask> subTasks = taskManager.getAllSubTasks();
        assertEquals(1, subTasks.size(), "Подзадача не удаляется.");
        assertEquals(Status.DONE, taskManager.getEpicTask(0).getStatus(), "Эпик не изменил статус на DONE.");

        taskManager.removeAllSubTasks();
        subTasks = taskManager.getAllSubTasks();
        assertEquals(0, subTasks.size(), "Все подзадачи не удалились.");
        assertEquals(Status.NEW, taskManager.getEpicTask(0).getStatus(), "Эпик не изменил статус на NEW.");
    }

    @Test
    void removeEpicTask() {
        taskManager.createEpicTask(epicTask1);
        EpicTask createdEpicTask1 = taskManager.getEpicTask(0);

        SubTask subTask1 = new SubTask("SubTask1", "SubTask1 description",
                Status.NEW, createdEpicTask1.getId());
        SubTask subTask2 = new SubTask("SubTask2", "SubTask2 description",
                Status.NEW, createdEpicTask1.getId());

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        taskManager.createEpicTask(epicTask2);
        EpicTask createdEpicTask2 = taskManager.getEpicTask(3);

        SubTask subTask3 = new SubTask("SubTask3", "SubTask3 description",
                Status.NEW, createdEpicTask2.getId());
        taskManager.createSubTask(subTask3);

        taskManager.removeTask(0);

        List<EpicTask> epicTasks = taskManager.getAllEpicTasks();
        List<SubTask> subTasks = taskManager.getAllSubTasks();
        assertEquals(1, epicTasks.size(), "Эпик не удалился.");
        assertEquals(1, subTasks.size(), "Подзадачи из эпика не удалились.");

        taskManager.removeAllEpicTasks();
        epicTasks = taskManager.getAllEpicTasks();
        subTasks = taskManager.getAllSubTasks();
        assertEquals(0, epicTasks.size(), "Все эпики не удалились.");
        assertEquals(0, subTasks.size(), "Подзадача из эпика не удалилась.");

    }


}