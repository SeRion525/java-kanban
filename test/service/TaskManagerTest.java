package service;

import exception.ValidationException;
import model.EpicTask;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static util.TaskTestUtil.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    protected abstract T createManager();

    @BeforeEach
    void init() {
        manager = createManager();
    }

    @DisplayName(" Создать задачу и присвоить ей id ")
    @Test
    void shouldCreateTaskWithId() {
        Task task = new Task("task", "task d", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        Task createdTask = manager.createTask(task);
        assertEqualsTask(task, createdTask, "Задачи не равны по");
    }

    @DisplayName(" Создать эпик с подазачей и присвоить им id ")
    @Test
    void shouldCreateEpicTaskAndSubTaskWithId() {
        EpicTask epicTask = new EpicTask("epicTask", "epicTask d");
        EpicTask createdEpicTask = manager.createEpicTask(epicTask);
        SubTask subTask = new SubTask("subTask", "subTask d", Status.NEW, createdEpicTask.getId(), LocalDateTime.now(),
                Duration.ofMinutes(5));
        SubTask createdSubTask = manager.createSubTask(subTask);

        assertEqualsEpicTask(epicTask, createdEpicTask, "Эпики не равны по");
        assertEqualsSubTask(subTask, createdSubTask, "Подзадачи не равны по");
    }

    @DisplayName("Вернуть список созданных задач, эпиков и подзадач")
    @Test
    void shouldReturnAllTasks() {
        Task task1 = new Task("task1", "task1 d", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        Task task2 = new Task("task2", "task2 d", Status.NEW, LocalDateTime.now().plusMinutes(1), Duration.ofMinutes(1));
        EpicTask epicTask1 = new EpicTask("epicTask1", "epicTask1 d");
        EpicTask epicTask2 = new EpicTask("epicTask2", "epicTask2 d");
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpicTask(epicTask1);
        manager.createEpicTask(epicTask2);

        SubTask subTask1 = new SubTask("subTask1", "subTask1 d", Status.NEW, epicTask1.getId(), LocalDateTime.now().plusMinutes(2),
                Duration.ofMinutes(1));
        SubTask subTask2 = new SubTask("subTask2", "subTask2 d", Status.NEW, epicTask2.getId(), LocalDateTime.now().plusMinutes(3),
                Duration.ofMinutes(1));
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        List<Task> tasksList = List.of(task1, task2);
        List<EpicTask> epicTasksList = List.of(epicTask1, epicTask2);
        List<SubTask> subTasksList = List.of(subTask1, subTask2);

        List<Task> createdTasks = manager.getAllTasks();
        List<EpicTask> createdEpicTasks = manager.getAllEpicTasks();
        List<SubTask> createdSubTasks = manager.getAllSubTasks();

        assertEquals(tasksList, createdTasks, "Вернулся неверный список задач");
        assertEquals(epicTasksList, createdEpicTasks, "Вернулся неверный список эпиков");
        assertEquals(subTasksList, createdSubTasks, "Вернулся неверный список подзадач");
    }

    @DisplayName("Вернуть список подзадач эпика по его id")
    @Test
    void shouldReturnSubTaskListFromEpicTask() {
        EpicTask epicTask = new EpicTask("epicTask", "epicTask d");
        EpicTask createdEpicTask = manager.createEpicTask(epicTask);
        SubTask subTask1 = new SubTask("subTask1", "subTask1 d", Status.NEW, createdEpicTask.getId(), LocalDateTime.now(),
                Duration.ofMinutes(1));
        SubTask subTask2 = new SubTask("subTask2", "subTask2 d", Status.NEW, createdEpicTask.getId(), LocalDateTime.now().plusMinutes(1),
                Duration.ofMinutes(1));
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        List<SubTask> subTasksList = List.of(subTask1, subTask2);
        List<SubTask> createdSubTasks = manager.getSubTasksFromEpicTaskId(createdEpicTask.getId());

        assertEquals(subTasksList, createdSubTasks, "Вернулся неверный список подзадач");
    }

    @DisplayName("Вернуть задачу, эпик и подзадачу по id")
    @Test
    void shouldReturnTaskById() {
        Task task1 = new Task("task1", "task1 d", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        EpicTask epicTask1 = new EpicTask("epicTask1", "epicTask1 d");
        manager.createTask(task1);
        manager.createEpicTask(epicTask1);

        SubTask subTask1 = new SubTask("subTask1", "subTask1 d", Status.NEW, epicTask1.getId(), LocalDateTime.now().plusMinutes(2),
                Duration.ofMinutes(1));
        manager.createSubTask(subTask1);

        Task task1Copy = copyTask(task1);
        EpicTask epicTaskCopy = (EpicTask) copyTask(epicTask1);
        SubTask subTaskCopy = (SubTask) copyTask(subTask1);

        Task createdTask = manager.getTask(task1Copy.getId());
        EpicTask createdEpicTask = manager.getEpicTask(epicTaskCopy.getId());
        SubTask createdSubTask = manager.getSubTask(subTaskCopy.getId());

        assertNotNull(createdTask, "Задача не вернулась");
        assertNotNull(createdEpicTask, "Эпик не вернулся");
        assertNotNull(createdSubTask, "Подзадача не вернулась");

        assertEqualsTask(task1Copy, manager.getTask(task1Copy.getId()), "Задачи не равны по");
        assertEqualsEpicTask(epicTaskCopy, manager.getEpicTask(epicTaskCopy.getId()), "Эпики не равны по");
        assertEqualsSubTask(subTaskCopy, manager.getSubTask(subTaskCopy.getId()), "Подзадачи не равны по");
    }

    @DisplayName("Обновить задачу ")
    @Test
    void shouldUpdateTask() {
        Task task = new Task("task", "task d", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        manager.createTask(task);

        Task updatedTask = copyTask(task);
        updatedTask.setStatus(Status.DONE);
        manager.updateTask(updatedTask);
        updatedTask = copyTask(updatedTask);

        assertEqualsTask(updatedTask, manager.getTask(updatedTask.getId()), "Задача не обновилась, разный");
    }

    @DisplayName("Обновить эпик ")
    @Test
    void shouldUpdateEpicTask() {
        EpicTask epicTask = new EpicTask("epicTask", "epicTask d");
        manager.createEpicTask(epicTask);

        EpicTask updatedEpicTask = (EpicTask) copyTask(epicTask);
        updatedEpicTask.setDescription("epicTask new d");
        manager.updateEpicTask(updatedEpicTask);
        updatedEpicTask = (EpicTask) copyTask(updatedEpicTask);

        assertEqualsEpicTask(updatedEpicTask, manager.getEpicTask(updatedEpicTask.getId()), "Эпик не обновился, разный");
    }

    @DisplayName("Обновить подзадачу ")
    @Test
    void shouldUpdateEpicTaskWithSubTasks() {
        EpicTask epicTask = new EpicTask("epicTask", "epicTask d");
        manager.createEpicTask(epicTask);
        SubTask subTask1 = new SubTask("subTask1", "subTask1 d", Status.NEW, epicTask.getId(), LocalDateTime.now(),
                Duration.ofMinutes(1));
        manager.createSubTask(subTask1);

        SubTask updatedSubTask1 = (SubTask) copyTask(subTask1);
        updatedSubTask1.setStatus(Status.DONE);
        manager.updateSubTask(updatedSubTask1);
        updatedSubTask1 = (SubTask) copyTask(updatedSubTask1);

        assertEqualsSubTask(updatedSubTask1, manager.getSubTask(updatedSubTask1.getId()), "Подзадача не обновилась, разный");
    }

    @DisplayName("Обновить статус в эпике")
    @Test
    void shouldUpdateEpicTaskStatus() {
        EpicTask epicTask = new EpicTask("epicTask", "epicTask d");
        manager.createEpicTask(epicTask);
        SubTask subTask1 = new SubTask("subTask1", "subTask1 d", Status.NEW, epicTask.getId(), LocalDateTime.now(),
                Duration.ofMinutes(1));
        manager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "subTask2 d", Status.NEW, epicTask.getId(), LocalDateTime.now().plusMinutes(1),
                Duration.ofMinutes(1));
        manager.createSubTask(subTask2);

        assertEquals(Status.NEW, manager.getEpicTask(epicTask.getId()).getStatus(),
                "Статус эпик не остался NEW, когда добавились подзадачи NEW");

        SubTask updatedSubTask1 = (SubTask) copyTask(subTask1);
        updatedSubTask1.setStatus(Status.DONE);
        manager.updateSubTask(updatedSubTask1);

        assertEquals(Status.IN_PROGRESS, manager.getEpicTask(epicTask.getId()).getStatus(),
                "Статус эпика не обновился на IN_PROGRESS, когда одна из подзадач DONE");

        SubTask updatedSubTask2 = (SubTask) copyTask(subTask2);
        updatedSubTask2.setStatus(Status.DONE);
        manager.updateSubTask(updatedSubTask2);

        assertEquals(Status.DONE, manager.getEpicTask(epicTask.getId()).getStatus(),
                "Статус эпика не обновился на DONE, когда все подзадачи DONE");

        SubTask subTask3 = new SubTask("subTask3", "subTask3 d", Status.IN_PROGRESS, epicTask.getId(), LocalDateTime.now().plusMinutes(2),
                Duration.ofMinutes(1));
        manager.createSubTask(subTask3);

        assertEquals(Status.IN_PROGRESS, manager.getEpicTask(epicTask.getId()).getStatus(),
                "Статус эпика не обновился на IN_PROGRESS, когда есть подзадача IN_PROGRESS");
    }

    @DisplayName("Обновить время у Эпика")
    @Test
    void shouldUpdateTimeOfEpicTask() {
        EpicTask epicTask = new EpicTask("epicTask", "epicTask d");
        manager.createEpicTask(epicTask);

        LocalDateTime startTimeOfSubTask1 = LocalDateTime.now();
        SubTask subTask1 = new SubTask("subTask1", "subTask1 d", Status.NEW, epicTask.getId(), startTimeOfSubTask1,
                Duration.ofMinutes(1));
        manager.createSubTask(subTask1);

        LocalDateTime startTimeOfSubTask2 = startTimeOfSubTask1.plusMinutes(1);
        SubTask subTask2 = new SubTask("subTask2", "subTask2 d", Status.NEW, epicTask.getId(), startTimeOfSubTask2,
                Duration.ofMinutes(1));
        manager.createSubTask(subTask2);

        assertEquals(subTask1.getStartTime(), epicTask.getStartTime(),
                "Время начала Эпика должна быть равна времяни начала ранней подзадачи");
        assertEquals(subTask2.getEndTime(), epicTask.getEndTime(),
                "Время конца Эпика должна быть равна времяни конца поздней подзадачи");
        assertEquals(Duration.ofMinutes(2), epicTask.getDuration(),
                "Длительность Эпика должна быть равна сумме длительности подзадач");
    }

    @DisplayName("Удалить задачу")
    @Test
    void shouldRemoveTask() {
        Task task = new Task("task", "task d", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        manager.createTask(task);
        manager.removeTask(task.getId());

        assertTrue(manager.getAllTasks().isEmpty(), "Задача не удалилась из менеджера");
        assertTrue(manager.getPrioritizedTasks().isEmpty(), "Задача не удалилась из коллекции задач с приоритетом");
    }

    @DisplayName("Удалить подзадачу и эпик")
    @Test
    void shouldRemoveSubTaskAndEpicTask() {
        EpicTask epicTask = new EpicTask("epicTask", "epicTask d");
        manager.createEpicTask(epicTask);
        SubTask subTask1 = new SubTask("subTask1", "subTask1 d", Status.NEW, epicTask.getId(), LocalDateTime.now(),
                Duration.ofMinutes(1));
        manager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "subTask2 d", Status.NEW, epicTask.getId(), LocalDateTime.now().plusMinutes(1),
                Duration.ofMinutes(1));
        manager.createSubTask(subTask2);

        manager.removeSubTask(subTask1.getId());
        assertEquals(1, manager.getAllSubTasks().size(), "Подзадача не удалилась из менеджера");
        assertEquals(1, manager.getEpicTask(epicTask.getId()).getSubTasksId().size(), "Подзадача не удалилась из эпика");

        manager.removeEpicTask(epicTask.getId());

        assertTrue(manager.getAllEpicTasks().isEmpty(), "Эпик не удалился");
        assertTrue(manager.getAllSubTasks().isEmpty(), "Подзадача не удалилась вместе с эпиком");
    }

    @DisplayName("Удалить все задачи")
    @Test
    void shouldRemoveAllTasks() {
        Task task1 = new Task("task1", "task1 d", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        Task task2 = new Task("task2", "task2 d", Status.NEW, LocalDateTime.now().plusMinutes(1), Duration.ofMinutes(1));
        manager.createTask(task1);
        manager.createTask(task2);
        manager.removeAllTasks();

        assertTrue(manager.getAllTasks().isEmpty(), "Все задачи не удалились");
    }

    @DisplayName("Удалить все эпики")
    @Test
    void shouldRemoveAllEpicTasks() {
        EpicTask epicTask1 = new EpicTask("epicTask1", "epicTask1 d");
        manager.createEpicTask(epicTask1);
        EpicTask epicTask2 = new EpicTask("epicTask2", "epicTask2 d");
        manager.createEpicTask(epicTask2);

        SubTask subTask1 = new SubTask("subTask1", "subTask1 d", Status.NEW, epicTask1.getId(), LocalDateTime.now(),
                Duration.ofMinutes(1));
        manager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "subTask2 d", Status.NEW, epicTask2.getId(), LocalDateTime.now().plusMinutes(1),
                Duration.ofMinutes(1));
        manager.createSubTask(subTask2);

        manager.removeAllEpicTasks();

        assertTrue(manager.getAllEpicTasks().isEmpty(), "Все эпики не удалились");
        assertTrue(manager.getAllSubTasks().isEmpty(), "Все подзадачи не удалились вместе с эпиками");
    }

    @DisplayName("Удалить все эпики")
    @Test
    void shouldRemoveAllSubTasks() {
        EpicTask epicTask = new EpicTask("epicTask", "epicTask d");
        manager.createEpicTask(epicTask);
        SubTask subTask1 = new SubTask("subTask1", "subTask1 d", Status.NEW, epicTask.getId(), LocalDateTime.now(),
                Duration.ofMinutes(1));
        manager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2", "subTask2 d", Status.NEW, epicTask.getId(), LocalDateTime.now().plusMinutes(1),
                Duration.ofMinutes(1));
        manager.createSubTask(subTask2);

        manager.removeAllSubTasks();

        assertTrue(manager.getAllSubTasks().isEmpty(), "Все подзадачи не удалились");
        assertTrue(manager.getEpicTask(epicTask.getId()).getSubTasksId().isEmpty(), "Подзадачи не удалились в эпике");
    }

    @DisplayName("Добваить задачу в историю")
    @Test
    void shouldAddTaskInHistory() {
        Task task1 = new Task("task1", "task1 d", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2));
        manager.createTask(task1);
        task1 = manager.getTask(task1.getId());

        List<Task> history = manager.getHistory();
        assertFalse(history.isEmpty(), "Задача не добавилась в историю");
        assertEqualsTask(task1, history.getFirst(), "Задачи не равны по");
    }

    @DisplayName("Пересечение по времени при создании задачи")
    @Test
    void shouldThrowExceptionWhenIsTimeIntersectionDuringCreate() {
        Task task1 = new Task("task1", "task1 d", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2));
        manager.createTask(task1);

        assertThrows(ValidationException.class, () ->
                        manager.createTask(new Task("task2", "task2 d", Status.NEW, task1.getStartTime(), Duration.ofMinutes(2))),
                "Не выбросилось исключение при одинаковом времени у разных задач");

        assertThrows(ValidationException.class, () ->
                        manager.createTask(new Task("task2", "task2 d", Status.NEW, task1.getStartTime().minusMinutes(1), Duration.ofMinutes(2))),
                "Не выбросилось исключение когда вторая задача началась раньше первой, но не закончилась");

        assertThrows(ValidationException.class, () ->
                        manager.createTask(new Task("task2", "task2 d", Status.NEW, task1.getStartTime().plusMinutes(1), Duration.ofMinutes(2))),
                "Не выбросилось исключение когда вторая задача началась позже первой, но первая не закончилась");

        assertDoesNotThrow(() ->
                        manager.createTask(new Task("task2", "task2 d", Status.NEW, task1.getStartTime().plusMinutes(2), Duration.ofMinutes(2))),
                "Выбросилось исключение при отсутствии пересечения");
    }

    @DisplayName("Пересечение по времени при обновлении задачи")
    @Test
    void shouldThrowExceptionWhenIsTimeIntersectionDuringUpdate() {
        Task task1 = new Task("task1", "task1 d", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2));
        manager.createTask(task1);

        assertDoesNotThrow(() -> {
                    Task updatedTask1 = copyTask(task1);
                    updatedTask1.setTitle("updatedTask1");
                    manager.updateTask(updatedTask1);
                },
                "Выбросилось исключение при неизменном времени");

        assertThrows(ValidationException.class, () -> {
                    Task updatedTask1 = new Task("task1", "task1 d", Status.NEW, task1.getStartTime().minusMinutes(1), Duration.ofMinutes(2));
                    updatedTask1.setId(task1.getId());
                    manager.updateTask(updatedTask1);
                },
                "Не выбросилось исключение когда вторая задача началась раньше первой, но не закончилась");

        assertThrows(ValidationException.class, () -> {
                    Task updatedTask1 = new Task("task1", "task1 d", Status.NEW, task1.getStartTime().plusMinutes(1), Duration.ofMinutes(2));
                    updatedTask1.setId(task1.getId());
                    manager.updateTask(updatedTask1);
                },
                "Не выбросилось исключение когда вторая задача началась позже первой, но первая не закончилась");

        assertDoesNotThrow(() -> {
                    Task updatedTask1 = new Task("task1", "task1 d", Status.NEW, task1.getStartTime().plusMinutes(2), Duration.ofMinutes(2));
                    updatedTask1.setId(task1.getId());
                    manager.updateTask(updatedTask1);
                },
                "Выбросилось исключение при отсутствии пересечения");
    }
}
