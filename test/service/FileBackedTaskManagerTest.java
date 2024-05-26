package service;

import model.EpicTask;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    private Path tempTaskFile;
    private Task task;
    private EpicTask epicTask;
    private SubTask subTask1;
    private SubTask subTask2;

    @Test
    void shouldSaveNewTasksInFile() {
        init();

        taskManager.createTask(task);
        taskManager.createEpicTask(epicTask);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        List<String> strings;

        try {
            strings = Files.readAllLines(tempTaskFile);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения из файла " + tempTaskFile, e);
        }

        assertEquals("id,type,title,status,description,epic", strings.get(0), "Строчка 1 не сошлась.");
        assertEquals("0,TASK,task,NEW,task discr,null", strings.get(1), "Строчка 2 не сошлась.");
        assertEquals("1,EPIC_TASK,epicTask,NEW,epicTask discr,null", strings.get(2), "Строчка 3 не сошлась.");
        assertEquals("2,SUB_TASK,subTask1,NEW,subTusk1 discr,1", strings.get(3), "Строчка 4 не сошлась.");
        assertEquals("3,SUB_TASK,subTask2,NEW,subTusk2 discr,1", strings.get(4), "Строчка 5 не сошлась.");

        try {
            Files.delete(tempTaskFile);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось удалить файл " + tempTaskFile, e);
        }

    }

    @Test
    void shouldLoadTasksFromFile() {
        init();

        taskManager.createTask(task);
        taskManager.createEpicTask(epicTask);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        taskManager = FileBackedTaskManager.loadFromFile(new File(tempTaskFile.toString()));
        List<Task> tasks = taskManager.getAllTasks();
        List<EpicTask> epicTasks = taskManager.getAllEpicTasks();
        List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertTasks(task, tasks.getFirst());
        assertTasks(epicTask, epicTasks.getFirst());
        assertTasks(subTask1, subTasks.get(0));
        assertTasks(subTask2, subTasks.get(1));

        try {
            Files.delete(tempTaskFile);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось удалить файл " + tempTaskFile, e);
        }
    }

    void init() {
        try {
            tempTaskFile = Files.createTempFile(Path.of("resources"), "tempTask", ".csv");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл " + tempTaskFile, e);
        }

        taskManager = new FileBackedTaskManager(tempTaskFile);

        task = new Task("task", "task discr", Status.NEW);
        task.setId(0);
        epicTask = new EpicTask("epicTask", "epicTask discr");
        epicTask.setId(1);
        subTask1 = new SubTask("subTask1", "subTusk1 discr", Status.NEW, 1);
        subTask1.setId(2);
        subTask2 = new SubTask("subTask2", "subTusk2 discr", Status.NEW, 1);
        subTask2.setId(3);
    }

    void assertTasks(Task task1, Task task2) {
        assertEquals(task1.getId(), task2.getId(),
                "У подзадач разный ID");
        assertEquals(task1.getTitle(), task2.getTitle(),
                "У подзадач разный заголовок");
        assertEquals(task1.getDescription(), task2.getDescription(),
                "У подзадач разное описание");
        assertEquals(task1.getStatus(), task2.getStatus(),
                "У подзадач разный статус");
        assertEquals(task1.getEpicTaskId(), task2.getEpicTaskId(),
                "У подзадач разные id эпика");
    }
}