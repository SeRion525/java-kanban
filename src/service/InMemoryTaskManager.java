package service;

import model.*;
import util.Managers;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int allTaskCount = 0;
    private final Map<Integer, Task> tasksById;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasksById = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();

        for (Task task : tasksById.values()) {
            if (TaskType.TASK.equals(task.getType())) {
                tasks.add(task);
            }
        }

        return tasks;
    }

    @Override
    public List<EpicTask> getAllEpicTasks() {
        List<EpicTask> epicTasks = new ArrayList<>();

        for (Task task : tasksById.values()) {
            if (TaskType.EPIC_TASK.equals(task.getType())) {
                epicTasks.add((EpicTask) task);
            }
        }

        return epicTasks;
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        List<SubTask> subTasks = new ArrayList<>();

        for (Task task : tasksById.values()) {
            if (TaskType.SUB_TASK.equals(task.getType())) {
                subTasks.add((SubTask) task);
            }
        }

        return subTasks;
    }

    @Override
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

    @Override
    public Task getTask(int id) {
        Task task = tasksById.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public EpicTask getEpicTask(int id) {
        Task task = tasksById.get(id);
        historyManager.add(task);
        return (EpicTask) task;
    }

    @Override
    public SubTask getSubTask(int id) {
        Task task = tasksById.get(id);
        historyManager.add(task);
        return (SubTask) task;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void createTask(Task task) {
        int taskId = giveId();
        task.setId(taskId);

        if (TaskType.SUB_TASK.equals(task.getType())) {
            SubTask subTask = (SubTask) task;
            EpicTask epicTask = (EpicTask) tasksById.get(subTask.getEpicTaskId());

            if (epicTask != null) {
                epicTask.addSubTaskId(subTask.getId());
                tasksById.put(taskId, task);
                updateEpicTaskStatus(epicTask.getId());
            }

        } else {
            tasksById.put(taskId, task);
        }
    }

    @Override
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

    @Override
    public void removeAllTasks() {
        for (Task task : tasksById.values()) {
            if (TaskType.TASK.equals(task.getType())) {
                tasksById.remove(task.getId());
                historyManager.remove(task.getId());
            }
        }
    }

    @Override
    public void removeAllEpicTasks() {
        List<Integer> epicTasksIds = new ArrayList<>();
        List<Integer> subTasksIds = new ArrayList<>();

        for (Task task : tasksById.values()) {
            if (TaskType.EPIC_TASK.equals(task.getType())) {
                EpicTask epicTask = (EpicTask) task;

                epicTasksIds.add(epicTask.getId());
                subTasksIds.addAll(epicTask.getSubTasksId());
            }
        }

        for (Integer epicTaskId : epicTasksIds) {
            tasksById.remove(epicTaskId);
            historyManager.remove(epicTaskId);
        }

        for (Integer subTaskId : subTasksIds) {
            tasksById.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
    }

    @Override
    public void removeAllSubTasks() {
        for (Task task : tasksById.values()) {
            if (TaskType.SUB_TASK.equals(task.getType())) {
                SubTask subTask = (SubTask) task;
                EpicTask epicTask = (EpicTask) tasksById.get(subTask.getEpicTaskId());

                epicTask.removeSubTask(subTask.getId());
                updateEpicTaskStatus(epicTask.getId());

                tasksById.remove(subTask.getId());
                historyManager.remove(subTask.getId());
            }
        }
    }

    @Override
    public void removeTask(int id) {
        Task task = tasksById.remove(id);

        if (task == null) {
            return;
        }

        historyManager.remove(id);

        if (TaskType.SUB_TASK.equals(task.getType())) {
            SubTask subTask = (SubTask) task;
            EpicTask epicTask = (EpicTask) tasksById.get(subTask.getEpicTaskId());

            epicTask.removeSubTask(subTask.getId());
            updateEpicTaskStatus(epicTask.getId());

        } else if (TaskType.EPIC_TASK.equals(task.getType())) {
            EpicTask epicTask = (EpicTask) task;
            for (int subTaskId : epicTask.getSubTasksId()) {
                tasksById.remove(subTaskId);
                historyManager.remove(subTaskId);
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
            } else if (Status.DONE.equals(subTaskStatus) && allSubTasksIsDone) {
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
