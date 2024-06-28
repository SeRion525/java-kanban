package service;

import exception.ManagerIOException;
import model.EpicTask;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static util.TaskTestUtil.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    protected Path tempTaskFile;

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(tempTaskFile);
    }

    @BeforeEach
    @Override
    void init() {
        try {
            tempTaskFile = Files.createTempFile(Path.of("resources"), "tempTask", ".csv");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл " + tempTaskFile, e);
        }

        manager = createManager();
    }

    @AfterEach
    void removeTempTaskFile() {
        try {
            Files.delete(tempTaskFile);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось удалить файл " + tempTaskFile, e);
        }
    }

    @Nested
    @DisplayName("Сохранение и загрузка задач из файла")
    class SaveAndLoadTaskFromFileTest {
        private Task task;
        private EpicTask epicTask;
        private SubTask subTask1;
        private SubTask subTask2;

        @BeforeEach
        void init() {
            task = new Task("task", "task discr", Status.NEW);
            task.setId(1);
            epicTask = new EpicTask("epicTask", "epicTask discr");
            epicTask.setId(2);
            subTask1 = new SubTask("subTask1", "subTusk1 discr", Status.NEW, epicTask.getId(),
                    LocalDateTime.now(), Duration.ofMinutes(1));
            subTask1.setId(3);
            subTask2 = new SubTask("subTask2", "subTusk2 discr", Status.NEW, epicTask.getId(),
                    LocalDateTime.now().plusMinutes(1), Duration.ofMinutes(1));
            subTask2.setId(4);
        }

        @DisplayName("Сохранить задачу в файл")
        @Test
        void shouldSaveNewTasksInFile() {

            manager.createTask(task);
            manager.createEpicTask(epicTask);
            manager.createSubTask(subTask1);
            manager.createSubTask(subTask2);

            List<String> strings;

            try {
                strings = Files.readAllLines(tempTaskFile);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка чтения из файла " + tempTaskFile, e);
            }

            assertEquals("id,type,title,status,description,epic,duration,startTime", strings.get(0),
                    "Строчка 1 не сошлась.");
            assertEquals("1,TASK,task,NEW,task discr,null,null,null", strings.get(1),
                    "Строчка 2 не сошлась.");
            assertEquals("2,EPIC_TASK,epicTask,NEW,epicTask discr,null," + epicTask.getDuration().toMinutes() + "," + epicTask.getStartTime().toString(),
                    strings.get(2), "Строчка 3 не сошлась.");
            assertEquals("3,SUB_TASK,subTask1,NEW,subTusk1 discr,2," + subTask1.getDuration().toMinutes() + "," + subTask1.getStartTime().toString(),
                    strings.get(3), "Строчка 4 не сошлась.");
            assertEquals("4,SUB_TASK,subTask2,NEW,subTusk2 discr,2," + subTask2.getDuration().toMinutes() + "," + subTask2.getStartTime().toString(),
                    strings.get(4), "Строчка 5 не сошлась.");
        }

        @DisplayName("Загрузить задачу из файла")
        @Test
        void shouldLoadTasksFromFile() {

            manager.createTask(task);
            manager.createEpicTask(epicTask);
            manager.createSubTask(subTask1);
            manager.createSubTask(subTask2);

            manager = FileBackedTaskManager.loadFromFile(new File(tempTaskFile.toString()));
            List<Task> tasks = manager.getAllTasks();
            List<EpicTask> epicTasks = manager.getAllEpicTasks();
            List<SubTask> subTasks = manager.getAllSubTasks();

            assertEqualsTask(task, tasks.getFirst(), "Задачи не равны");
            assertEqualsEpicTask(epicTask, epicTasks.getFirst(), "Эпики не равны");
            assertEqualsSubTask(subTask1, subTasks.get(0), "Подзадачи не равны");
            assertEqualsSubTask(subTask2, subTasks.get(1), "Подзадачи не равны");
        }

        @DisplayName("Перехват исключений при работе с файлами")
        @Test
        void shouldThrowExceptionDuringUseFiles() {
            assertThrows(ManagerIOException.class, () -> {
                        TaskManager testManager = new FileBackedTaskManager(Paths.get("i_hate_tests/really.csv"));
                        testManager.createTask(task);
                    },
                    "Некорректный перехват исключения при сохранении задач в файл");

            assertThrows(ManagerIOException.class, () -> FileBackedTaskManager.loadFromFile(new File("D:/")),
                    "Некорректный перехват исключения при загрузке задач из файла");
        }
    }
}