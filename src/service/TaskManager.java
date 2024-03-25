package service;

import model.EpicTask;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private static int allTaskCount = 0;
    private final Map<Integer, Task> tasksById;

    public TaskManager() {
        this.tasksById = new HashMap<>();
    }

    public static int giveId() {
        int id = allTaskCount;
        allTaskCount++;
        return id;
    }

    public List<Task> getAll() {
        List<Task> tasks = new ArrayList<>(tasksById.values());
        return tasks;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();

        for (Task task : tasksById.values()) {
            if (!(task instanceof EpicTask) && !(task instanceof SubTask)) {
                tasks.add(task);
            }
        }

        return tasks;
    }

    public List<EpicTask> getAllEpicTasks() {
        List<EpicTask> epicTasks = new ArrayList<>();

        for (Task task : tasksById.values()) {
            if (task instanceof EpicTask) {
                epicTasks.add((EpicTask) task);
            }
        }

        return epicTasks;
    }

    public List<SubTask> getAllSubTasks() {
        List<SubTask> subTasks = new ArrayList<>();

        for (Task task : tasksById.values()) {
            if (task instanceof SubTask) {
                subTasks.add((SubTask) task);
            }
        }

        return subTasks;
    }

    public List<SubTask> getSubTasksFromEpicTask(EpicTask inputEpicTask) {
        EpicTask epicTask = (EpicTask) tasksById.get(inputEpicTask.getId());

        if (epicTask != null) {
            return epicTask.getSubTasks();
        }

        return null;
    }

    public Task getTask(int id) {
        Task task = tasksById.get(id);
        return task;
    }

    public void createTask(Task task) {
        tasksById.put(task.getId(), task);

        if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            EpicTask epicTask = (EpicTask) tasksById.get(subTask.getEpic().getId());

            if (epicTask != null) {
                epicTask.addSubTask(subTask);
            }
        }
    }

    public void updateTask(Task task) {
        tasksById.put(task.getId(), task);

        if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            EpicTask epicTask = (EpicTask) tasksById.get(subTask.getEpic().getId());

            if (epicTask != null) {
                epicTask.addSubTask(subTask);
                subTask.setEpicTask(epicTask);
                epicTask.updateStatus();
            }
        } else if (task instanceof EpicTask) {
            EpicTask epicTask = (EpicTask) task;

            for (SubTask subTask : epicTask.getSubTasks()) {
                tasksById.put(subTask.getId(), subTask);
            }

            epicTask.updateStatus();
        }
    }

    public void removeAllTasks() {
        tasksById.clear();
    }

    public void removeTask(int id) {
        Task task = tasksById.remove(id);

        if (task instanceof SubTask) {
            SubTask subTask = ((SubTask) task);
            EpicTask epicTask = subTask.getEpic();
            epicTask.removeSubTask(subTask);
            epicTask.updateStatus();
        } else if (task instanceof EpicTask) {
            EpicTask epicTask = (EpicTask) task;

            for (int subTaskId : epicTask.getSubTasksId()) {
                tasksById.remove(subTaskId);
            }

            epicTask.removeAllSubTasks();
        }
    }
}
