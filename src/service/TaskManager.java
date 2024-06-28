package service;

import model.EpicTask;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    EpicTask createEpicTask(EpicTask epicTask);

    SubTask createSubTask(SubTask subTask);

    List<Task> getPrioritizedTasks();

    List<Task> getAllTasks();

    List<EpicTask> getAllEpicTasks();

    List<SubTask> getAllSubTasks();

    List<SubTask> getSubTasksFromEpicTaskId(int epicTaskId);

    Task getTask(int id);

    EpicTask getEpicTask(int id);

    SubTask getSubTask(int id);

    List<Task> getHistory();

    Task updateTask(Task task);

    EpicTask updateEpicTask(EpicTask epicTask);

    SubTask updateSubTask(SubTask subTask);

    void removeAllTasks();

    void removeAllEpicTasks();

    void removeAllSubTasks();

    void removeTask(int id);

    void removeEpicTask(int id);

    void removeSubTask(int id);
}
