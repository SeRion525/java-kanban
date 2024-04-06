package service;

import model.EpicTask;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    List<EpicTask> getAllEpicTasks();

    List<SubTask> getAllSubTasks();

    List<SubTask> getSubTasksFromEpicTaskId(int epicTaskId);

    Task getTask(int id);

    EpicTask getEpicTask(int id);

    SubTask getSubTask(int id);

    List<Task> getHistory();

    void createTask(Task task);

    void updateTask(Task task);

    void removeAllTasks();

    void removeAllEpicTasks();

    void removeAllSubTasks();

    void removeTask(int id);
}
