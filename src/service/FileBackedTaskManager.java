package service;

import exception.ManagerSaveException;
import model.*;
import util.TaskToStringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path filePath;

    public FileBackedTaskManager() {
        this.filePath = Paths.get("resources/task.csv");

        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
            } catch (IOException exception) {
                throw new ManagerSaveException("Не смог создать файл " + filePath, exception);
            }
        }
    }

    public FileBackedTaskManager(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpicTask(EpicTask epicTask) {
        super.createEpicTask(epicTask);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        super.updateEpicTask(epicTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpicTasks() {
        super.removeAllEpicTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    private void save() {
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(getAllTasks());
        tasks.addAll(getAllEpicTasks());
        tasks.addAll(getAllSubTasks());

        try {
            Files.writeString(filePath, "id,type,title,status,description,epic\n");
            for (Task task : tasks) {
                Files.writeString(filePath, TaskToStringConverter.toString(task) + "\n", APPEND);
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи в файл " + filePath, exception);
        }
    }

    private void loadFromFile() {
        List<String> strings;
        int lastId = 0;

        try {
            strings = Files.readAllLines(filePath);
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка чтения из файла " + filePath, exception);
        }

        strings.removeFirst();

        for (String string : strings) {
            String[] taskData = string.split(",");
            int id = Integer.parseInt(taskData[0]);
            TaskType type = TaskType.valueOf(taskData[1]);
            String title = taskData[2];
            Status status = Status.valueOf(taskData[3]);
            String description = taskData[4];
            int epicTaskId;

            lastId = Math.max(id, lastId);

            switch (type) {
                case TASK:
                    Task task = new Task(title, description, status);
                    task.setId(id);
                    tasksById.put(id, task);
                    break;
                case EPIC_TASK:
                    EpicTask epicTask = new EpicTask(title, description);
                    epicTask.setId(id);
                    epicTasksById.put(id, epicTask);
                    break;
                case SUB_TASK:
                    epicTaskId = Integer.parseInt(taskData[5]);
                    SubTask subTask = new SubTask(title, description, status, epicTaskId);
                    subTask.setId(id);
                    subTasksById.put(id, subTask);
                    break;
            }
        }

        allTaskCount = lastId + 1;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file.toPath());
        taskManager.loadFromFile();
        return taskManager;
    }
}
