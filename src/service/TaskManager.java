package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private int allTaskCount = 0;
    private final Map<Integer, Task> tasksById;

    public TaskManager() {
        this.tasksById = new HashMap<>();
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();

        for (Task task : tasksById.values()) {
            if (TaskType.TASK.equals(task.getType())) {
                tasks.add(task);
            }
        }

        return tasks;
    }

    public List<EpicTask> getAllEpicTasks() {
        List<EpicTask> epicTasks = new ArrayList<>();

        for (Task task : tasksById.values()) {
            if (TaskType.EPIC_TASK.equals(task.getType())) {
                epicTasks.add((EpicTask) task);
            }
        }

        return epicTasks;
    }

    public List<SubTask> getAllSubTasks() {
        List<SubTask> subTasks = new ArrayList<>();

        for (Task task : tasksById.values()) {
            if (TaskType.SUB_TASK.equals(task.getType())) {
                subTasks.add((SubTask) task);
            }
        }

        return subTasks;
    }

    public List<SubTask> getSubTasksFromEpicTaskId(int epicTaskId) {
        EpicTask epicTask = (EpicTask) tasksById.get(epicTaskId);

        if (epicTask != null) {
            List<Integer> subTasksId = epicTask.getSubTasksId();
            List<SubTask> subTasks = new ArrayList<>();

            for (int subTaskId : subTasksId) {
                SubTask subTask = (SubTask) tasksById.get(subTaskId);
                if (subTask != null) {
                    subTasks.add(subTask);
                }
            }

            return subTasks;
        }

        return null;
    }

    public Task getTask(int id) {
        return tasksById.get(id);
    }
    public EpicTask getEpicTask(int id) {
        return (EpicTask) tasksById.get(id);
    }
    public SubTask getSubTask(int id) {
        return (SubTask) tasksById.get(id);
    }

    public void createTask(Task task) {
        int taskId = giveId();
        task.setId(taskId);

        if (TaskType.SUB_TASK.equals(task.getType())) {
            SubTask subTask = (SubTask) task;
            EpicTask epicTask = (EpicTask) tasksById.get(subTask.getEpicTaskId());

            if (epicTask != null) {
                epicTask.addSubTaskId(subTask.getId());
                tasksById.put(taskId, task);
            }

        } else {
            tasksById.put(taskId, task);
        }
    }

    public void updateTask(Task task) {
        tasksById.put(task.getId(), task);

        if (TaskType.SUB_TASK.equals(task.getType())) {
            SubTask subTask = (SubTask) task;
            EpicTask epicTask = (EpicTask) tasksById.get(subTask.getEpicTaskId());

            if (epicTask != null) {
                updateEpicTaskStatus(epicTask.getId());
            }
        }
    }

    public void removeAllTasks() {
        for (Task task : tasksById.values()) {
            if (TaskType.TASK.equals(task.getType())) {
                tasksById.remove(task.getId());
            }
        }
    }
    public void removeAllEpicTasks() {
        for (Task task : tasksById.values()) {
            if (TaskType.EPIC_TASK.equals(task.getType())) {
                EpicTask epicTask = (EpicTask) task;

                for (int subTaskId : epicTask.getSubTasksId()) {
                    tasksById.remove(subTaskId);
                }

                tasksById.remove(epicTask.getId());
            }
        }
    }

    public void removeAllSubTasks() {
        for (Task task : tasksById.values()) {
            if (TaskType.SUB_TASK.equals(task.getType())) {
                SubTask subTask = (SubTask) task;
                EpicTask epicTask = (EpicTask) tasksById.get(subTask.getEpicTaskId());

                epicTask.removeSubTask(subTask.getId());
                updateEpicTaskStatus(epicTask.getId());

                tasksById.remove(subTask.getId());
            }
        }
    }

    public void removeTask(int id) {
        Task task = tasksById.remove(id);

        if (task == null) {
            return;
        }

        if (TaskType.SUB_TASK.equals(task.getType())) {
            SubTask subTask = (SubTask) task;
            EpicTask epicTask = (EpicTask) tasksById.get(subTask.getEpicTaskId());

            epicTask.removeSubTask(subTask.getId());

        } else if (TaskType.EPIC_TASK.equals(task.getType())) {
            EpicTask epicTask = (EpicTask) task;
            for (int subTaskId : epicTask.getSubTasksId()) {
                tasksById.remove(subTaskId);
            }
        }
    }

    private int giveId() {
        int id = allTaskCount;
        allTaskCount++;
        return id;
    }

    private void updateEpicTaskStatus(int id) {
        EpicTask epicTask = (EpicTask) tasksById.get(id);
        List<Integer> subTasksIdList = epicTask.getSubTasksId();

        if (subTasksIdList.isEmpty()) {
            epicTask.setStatus(Status.NEW);
            return;
        }

        boolean allSubTasksIsNew = true;
        boolean allSubTasksIsDone = true;

        for (int subTaskId : subTasksIdList) {
            Status subTaskStatus = tasksById.get(subTaskId).getStatus();

            if (Status.NEW.equals(subTaskStatus) && allSubTasksIsNew) {
                allSubTasksIsDone = false;
            } else if (Status.DONE.equals(subTaskStatus) && allSubTasksIsDone){
                allSubTasksIsNew = false;
            } else {
                allSubTasksIsNew = false;
                allSubTasksIsDone = false;
                break;
            }
        }

        if (allSubTasksIsNew) {
            epicTask.setStatus(Status.NEW);
        } else if (allSubTasksIsDone) {
            epicTask.setStatus(Status.DONE);
        } else {
            epicTask.setStatus(Status.IN_PROGRESS);
        }

    }
}
